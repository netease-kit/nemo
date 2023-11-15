package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.dto.message.OrderSongNotifyEventDto;
import com.netease.nemo.entlive.enums.KtvSingStatusEnum;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.enums.OrderSongStatusEnum;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.entlive.wrapper.OrderSongMapperWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.paramters.neroom.NeRoomMessageParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点歌台服务
 *
 * @Author：CH
 * @Date：2023/5/19 5:59 下午
 */
@Service
@Slf4j
public class OrderSongServiceImpl implements OrderSongService {

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private UserService userService;

    @Resource
    private OrderSongMapperWrapper orderSongMapperWrapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private MusicPlayService musicPlayService;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private SingService singService;

    @Value("${business.roomOrderSongLimit:20}")
    private Integer roomOrderSongLimit;

    @Value("${business.ktvRoomOrderSongLimit:10}")
    private Integer ktvRoomOrderSongLimit;

    @Value("${business.userOrderSongLimit:2}")
    private Integer userOrderSongLimit;

    @Override
    public int addOrderSong(OrderSong orderSong) {
        int res = orderSongMapperWrapper.insertSelective(orderSong);
        if (res < 1) {
            log.error("add OrderSong failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @Override
    @Transactional
    public OrderSongResultDto orderSong(OrderSongDto orderSongDto) {
        LiveRecordDto liveRecordDto = getLiveRecordDto(orderSongDto.getLiveRecordId());

        Long liveRecordId = liveRecordDto.getId();
        String roomArchiveId = liveRecordDto.getRoomArchiveId();
        String userUuid = orderSongDto.getUserUuid();
        String roomUuid = liveRecordDto.getRoomUuid();

        if (LiveTypeEnum.CHAT.getType() == liveRecordDto.getLiveType()
                && !liveRecordDto.getUserUuid().equals(userUuid)) {
            throw new BsException(ErrorCode.FORBIDDEN, "非主播不能点歌");
        }
        boolean isFirstOrder = isFirstOrder(liveRecordId);
        // 校验点歌数量及权限
        if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            checkOrderSongForKtv(liveRecordId, roomArchiveId, userUuid, orderSongDto.getSongId());
        } else {
            checkOrderSong(liveRecordId, roomArchiveId, userUuid, orderSongDto.getSongId());
        }

        UserDto orderSongUser = userService.getUser(userUuid);
        orderSongDto.setRoomArchiveId(roomArchiveId);
        orderSongDto.setRoomUuid(roomUuid);
        OrderSong orderSong = modelMapper.map(orderSongDto, OrderSong.class);

        // 添加点歌到队列
        addOrderSong(orderSong);

        // 返回结果
        OrderSongResultDto orderSongResultDto = OrderSongResultDto.build(orderSong, orderSongUser);

        // 构造OrderSongNotifyEvent
        OrderSongNotifyEventDto orderSongNotify = OrderSongNotifyEventDto
                .builder()
                .orderSongResultDto(orderSongResultDto)
                .operatorUser(BasicUserDto.buildBasicUser(orderSongUser))
                .build();

        // 发送点歌事件
        sendNotifyMsg(roomUuid, EventTypeEnum.ENT_USER_ORDER_SONG.getType(), orderSongNotify);
        // 发送点歌队列变化事件
        sendOrderSongChangeNotifyMsg(roomUuid, null);

        // ktv模式下，首次点歌时直接播放该歌曲
        if (LiveTypeEnum.KTV.getType() == liveRecordDto.getLiveType() && isFirstOrder) {
            singService.playNextSongMsg(userUuid, roomUuid, orderSongResultDto);
        }

        return orderSongResultDto;
    }

    private void checkOrderSong(Long liveRecordId, String roomArchiveId, String userUuid, String songId) {
        if (!neRoomMemberService.userInNeRoom(roomArchiveId, userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordIdAndUserId(liveRecordId, userUuid);
        if (!CollectionUtils.isEmpty(orderSongs)) {
            boolean isAlreadyOrder = orderSongs.stream().anyMatch(o -> (o.getSongId().equals(songId) && OrderSongStatusEnum.effectiveOrderSong(o.getStatus())));
            if (isAlreadyOrder) {
                throw new BsException(ErrorCode.SONG_IS_ALREADY_ORDER, "The song " + songId + " is already order");
            }
        }

        int roomOrderSongCount = orderSongMapperWrapper.selectOrderSongCount(liveRecordId);
        if (roomOrderSongCount >= roomOrderSongLimit) {
            throw new BsException(ErrorCode.ROOM_ORDER_SONG_EXCEED_LIMIT);
        }
    }

    private void checkOrderSongForKtv(Long liveRecordId, String roomArchiveId, String userUuid, String songId) {
        if (!neRoomMemberService.userInNeRoom(roomArchiveId, userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        if (!neRoomMemberService.isUserOnSeat(roomArchiveId, userUuid)) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户不在麦上");
        }
        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordIdAndUserIdForKtv(liveRecordId, userUuid);
        if (!CollectionUtils.isEmpty(orderSongs)) {
            boolean isAlreadyOrder = orderSongs.stream().anyMatch(o -> (o.getSongId().equals(songId) && OrderSongStatusEnum.effectiveOrderSongForKtv(o.getStatus())));
            if (isAlreadyOrder) {
                throw new BsException(ErrorCode.SONG_IS_ALREADY_ORDER, "The song " + songId + " is already order");
            }
            if (orderSongs.size() >= userOrderSongLimit) {
                throw new BsException(ErrorCode.USER_ORDER_SONG_EXCEED_LIMIT);
            }
        }

        int roomOrderSongCount = orderSongMapperWrapper.selectOrderSongCountForKtv(liveRecordId);
        if (roomOrderSongCount >= ktvRoomOrderSongLimit) {
            throw new BsException(ErrorCode.ROOM_ORDER_SONG_EXCEED_LIMIT);
        }
    }

    private boolean isFirstOrder(Long liveRecordId) {
        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordIdForKtv(liveRecordId);
        return CollectionUtils.isEmpty(orderSongs);
    }

    @Override
    @Transactional
    public void songSetTop(Long liveRecordId, String operator, Long orderId) {
        LiveRecordDto liveRecordDto = getLiveRecordDto(liveRecordId);
        OrderSong orderSong = getOrderSong(orderId);

        String roomUuid = liveRecordDto.getRoomUuid();
        String userUuid = orderSong.getUserUuid();

        checkOperatorPermission(operator, liveRecordDto, userUuid);

        if (!neRoomMemberService.userInNeRoom(orderSong.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        if (OrderSongStatusEnum.CANCEL.getCode() == orderSong.getStatus()) {
            throw new BsException(ErrorCode.ORDER_SONG_HAS_CANCELLED, "The Song Has Been Cancelled");
        }

        // 设置置顶时间
        orderSong.setSetTopTime(System.currentTimeMillis());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);

        // 返回结果
        UserDto operatorUser = userService.getUser(operator);
        OrderSongResultDto orderSongResultDto = OrderSongResultDto.build(orderSong, userService.getUser(userUuid));

        // 构造OrderSongNotifyEvent
        OrderSongNotifyEventDto orderSongNotify = OrderSongNotifyEventDto
                .builder()
                .orderSongResultDto(orderSongResultDto)
                .operatorUser(BasicUserDto.buildBasicUser(operatorUser))
                .build();

        // 发送点歌事件
        sendNotifyMsg(roomUuid, EventTypeEnum.ENT_USER_SONG_SET_TOP.getType(), orderSongNotify);
        // 发送点歌事件
        sendOrderSongChangeNotifyMsg(roomUuid, null);
    }


    @Override
    @Transactional
    public void cancelOrderSong(Long liveRecordId, String operator, Long orderId) {
        LiveRecordDto liveRecordDto = getLiveRecordDto(liveRecordId);
        OrderSong orderSong = getOrderSong(orderId);

        String roomUuid = liveRecordDto.getRoomUuid();
        String userUuid = orderSong.getUserUuid();

        checkOperatorPermission(operator, liveRecordDto, userUuid);

        if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            SingDetailInfoDto singDetailInfoDto = singService.getSingInfo(liveRecordDto.getRoomUuid());
            if (singDetailInfoDto != null && (singDetailInfoDto.getOrderId().equals(orderId) && KtvSingStatusEnum.PLAY.getStatus() == singDetailInfoDto.getSongStatus())) {
                throw new BsException(ErrorCode.FORBIDDEN, "当前歌曲正在演唱中，不可被取消");
            }
        }

        if (!neRoomMemberService.userInNeRoom(orderSong.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        if (OrderSongStatusEnum.CANCEL.getCode() == orderSong.getStatus()) {
            throw new BsException(ErrorCode.ORDER_SONG_HAS_CANCELLED, "The Song Has Been Cancelled");
        }

        UserDto operatorUser = userService.getUser(operator);

        // 设置删除状态
        orderSong.setStatus(OrderSongStatusEnum.CANCEL.getCode());

        // 返回结果
        OrderSongResultDto orderSongResultDto = OrderSongResultDto.build(orderSong, userService.getUser(userUuid));

        // 构造OrderSongNotifyEvent
        OrderSongNotifyEventDto orderSongNotify = OrderSongNotifyEventDto
                .builder()
                .orderSongResultDto(orderSongResultDto)
                .operatorUser(BasicUserDto.buildBasicUser(operatorUser))
                .build();

        OrderSong nextOrderSong = getNextOrderId(liveRecordId, orderId);
        if (nextOrderSong != null && !nextOrderSong.getId().equals(orderId)) {
            orderSongNotify.setNextOrderSong(OrderSongResultDto.build(nextOrderSong, userService.getUser(nextOrderSong.getUserUuid())));
        }
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);
        musicPlayService.cleanPlayerMusicInfo(liveRecordId, orderId);

        // 发送点歌事件
        sendNotifyMsg(roomUuid, EventTypeEnum.ENT_USER_CANCEL_ORDER_SONG.getType(), orderSongNotify);
        // 发送点歌事件
        sendOrderSongChangeNotifyMsg(roomUuid, null);
    }

    private void checkOperatorPermission(String operator, LiveRecordDto liveRecordDto, String userUuid) {
        if (LiveTypeEnum.isChatLive(liveRecordDto.getLiveType()) || LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            if (!operator.equals(userUuid) && !operator.equals(liveRecordDto.getUserUuid())) {
                throw new BsException(ErrorCode.FORBIDDEN, "无权限");
            }
        }
    }

    @Override
    @Transactional
    public void orderSongSwitch(Long liveRecordId, String operator, Long orderId, String attachment) {
        LiveRecordDto liveRecordDto = getLiveRecordDto(liveRecordId);
        OrderSong orderSong = getOrderSong(orderId);

        String roomUuid = liveRecordDto.getRoomUuid();
        String userUuid = orderSong.getUserUuid();

        checkOperatorPermission(operator, liveRecordDto, userUuid);

        if (!neRoomMemberService.userInNeRoom(orderSong.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        if (OrderSongStatusEnum.CANCEL.getCode() == orderSong.getStatus()) {
            throw new BsException(ErrorCode.ORDER_SONG_HAS_CANCELLED, "The Song Has Been Cancelled");
        }

        if(LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType()) && OrderSongStatusEnum.PLAYING.getCode() != orderSong.getStatus()) {
            throw new BsException(ErrorCode.FORBIDDEN, "当前歌曲不在演唱中，不可被切歌");
        }

        // 标记为已唱
        orderSong.setStatus(OrderSongStatusEnum.PLAYED.getCode());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);

        OrderSong nextOrderSong = getNextOrderId(liveRecordId, orderId);
        log.info("the nextOrderSong is:{}", GsonUtil.toJson(nextOrderSong));

        UserDto operatorUser = userService.getUser(operator);
        OrderSongResultDto nextOrderSongResult = null;
        if (nextOrderSong != null) {
            nextOrderSongResult = OrderSongResultDto.build(nextOrderSong, userService.getUser(nextOrderSong.getUserUuid()));
            log.info("the nextOrderSong is:{}", GsonUtil.toJson(nextOrderSongResult));
        }

        musicPlayService.cleanPlayerMusicInfo(liveRecordId, orderId);

        // 返回结果
        OrderSongResultDto orderSongResultDto = OrderSongResultDto.build(orderSong, userService.getUser(orderSong.getUserUuid()));

        // 构造OrderSongNotifyEvent
        OrderSongNotifyEventDto orderSongNotify = OrderSongNotifyEventDto
                .builder()
                .orderSongResultDto(orderSongResultDto)
                .nextOrderSong(nextOrderSongResult)
                .attachment(attachment)
                .operatorUser(BasicUserDto.buildBasicUser(operatorUser))
                .build();

        // 发送点歌事件
        sendNotifyMsg(roomUuid, EventTypeEnum.ENT_USER_SWITCH_SONG.getType(), orderSongNotify);

        // 发送点歌对列变化事件
        sendOrderSongChangeNotifyMsg(roomUuid, null);

        // KTV切歌
        if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            singService.endSingWhenSongSwitch(roomUuid, operator, orderId);
        }
    }

    @Override
    public List<OrderSongResultDto> getOrderSongs(Long liveRecordId) {
        List<OrderSong> orderSongs;
        LiveRecordDto liveRecordDto = getLiveRecordDto(liveRecordId);
        if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            orderSongs = orderSongMapperWrapper.selectByLiveRecordIdForKtv(liveRecordId);
        } else {
            orderSongs = orderSongMapperWrapper.selectByLiveRecordId(liveRecordId);

        }
        return orderSongs.stream().map(o -> {
            UserDto userDto = userService.getUser(o.getUserUuid());
            return OrderSongResultDto.build(o, userDto);
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderSongResultDto> getUserOrderSongs(Long liveRecordId, String userUuid) {
        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordIdAndUserId(liveRecordId, userUuid);
        return orderSongs.stream().map(o -> {
            UserDto userDto = userService.getUser(o.getUserUuid());
            return OrderSongResultDto.build(o, userDto);
        }).collect(Collectors.toList());
    }

    @Override
    public void cleanOrderSongs(Long liveRecordId) {
        orderSongMapperWrapper.cleanOrderSongs(liveRecordId);
    }

    @Override
    public void cleanUserOrderSongs(Long liveRecordId, String userUuid) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        boolean isLiving = liveRecordDto != null && LiveEnum.isLive(liveRecordDto.getLive());
        int res = orderSongMapperWrapper.cleanOrderSongsByUserUuid(liveRecordId, userUuid);
        if (res > 0 && isLiving) {
            // 发送点歌队列变化事件
            sendOrderSongChangeNotifyMsg(liveRecordDto.getRoomUuid(), null);
        }
    }

    public OrderSong getAndCheckKtvOrderSong(String operator, Long orderId) {
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSongForKtv(orderSong.getStatus())) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }
        // 检查操作人是否为点歌人
        if (!operator.equals(orderSong.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }
        return orderSong;    }


    /**
     * 点歌相关消息通知
     *
     * @param roomUuid                房间编号
     * @param eventType               点歌事件
     * @param orderSongNotifyEventDto 点歌信息
     */
    private void sendNotifyMsg(String roomUuid, Integer eventType, OrderSongNotifyEventDto orderSongNotifyEventDto) {
        //构建点歌广播消息
        EventDto eventDto = new EventDto(orderSongNotifyEventDto, eventType);

        NeRoomMessageParam messageParam = NeRoomMessageParam
                .builder()
                .roomUuid(roomUuid)
                .message(GsonUtil.toJson(eventDto))
                .build();

        neRoomService.sendNeRoomCustomMessage(messageParam);
    }


    /**
     * 发送点歌列表变化事件
     *
     * @param roomUuid        房间编号
     * @param orderSongNotify 消息体
     */
    private void sendOrderSongChangeNotifyMsg(String roomUuid, OrderSongNotifyEventDto orderSongNotify) {
        sendNotifyMsg(roomUuid, EventTypeEnum.ENT_ORDER_SONG_LIST_CHANGE.getType(), orderSongNotify);
    }

    /**
     * 获取当前歌曲的下一首歌
     *
     * @param liveRecordId 直播编号
     * @param orderId      当前歌曲点歌ID
     * @return
     */
    private OrderSong getNextOrderId(Long liveRecordId, Long orderId) {
        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordId(liveRecordId);
        if (CollectionUtils.isEmpty(orderSongs)) {
            return null;
        }

        if (orderSongs.size() == 1) {
            return orderSongs.get(0);
        }

        int index = -1;
        for (int i = 0; i < orderSongs.size(); i++) {
            if (orderSongs.get(i).getId().equals(orderId)) {
                index = i;
                break;
            }
        }

        if (index == orderSongs.size() - 1) {
            return orderSongs.get(0);
        }

        if (index != -1) {
            return orderSongs.get(index + 1);
        }
        return null;
    }

    private LiveRecordDto getLiveRecordDto(Long liveRecordId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        return liveRecordDto;
    }

    private OrderSong getOrderSong(Long orderId) {
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
        if (orderSong == null) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }
        return orderSong;
    }
}
