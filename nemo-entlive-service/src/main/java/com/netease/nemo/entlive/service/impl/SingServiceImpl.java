package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.PlayNextSongTask;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.enums.*;
import com.netease.nemo.entlive.model.po.ChorusRecord;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.entlive.parameter.AbandonSingParam;
import com.netease.nemo.entlive.parameter.SingActionParam;
import com.netease.nemo.entlive.parameter.SingParam;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.entlive.wrapper.ChorusRecordMapperWrapper;
import com.netease.nemo.entlive.wrapper.OrderSongMapperWrapper;
import com.netease.nemo.entlive.wrapper.SingRedisWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.queue.producer.DelayQueueProducer;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 演唱服务实现类
 *
 * @Author：CH
 * @Date：2023/10/11 4:33 PM
 */
@Service
@Slf4j
public class SingServiceImpl implements SingService {

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private ChorusRecordService chorusRecordService;

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @Resource
    private SingRedisWrapper singRedisWrapper;

    @Resource
    private OrderSongMapperWrapper orderSongMapperWrapper;

    @Resource
    private OrderSongService orderSongService;

    @Resource
    private ChorusRecordMapperWrapper chorusRecordMapperWrapper;

    @Resource
    private ChorusService chorusService;

    @Resource
    private DelayQueueProducer delayQueueProducer;

    @Override
    public void singStart(String operator, SingParam param) {
        String roomUuid = param.getRoomUuid();
        String chorusId = param.getChorusId();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        if (!neRoomMemberService.isUserOnSeat(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户不在麦上");
        }

        SingBaseInfoDto singInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singInfo == null) {
            throw new BsException(ErrorCode.SING_INFO_EMPTY);
        }

        if (singInfo.getSongStatus() == KtvSingStatusEnum.PLAY.getStatus()) {
            throw new BsException(ErrorCode.SING_PROCESSING);
        }
        boolean isChorus = StringUtils.isNoneBlank(chorusId);
        if (!isChorus) {
            // 独唱逻辑
            startSoloSing(operator, singInfo, param.getExt(), liveRecordDto);
        } else {
            startChorusSing(operator, singInfo, param, liveRecordDto);
        }
    }

    private void startChorusSing(String operator, SingBaseInfoDto singInfo, SingParam param, LiveRecordDto liveRecordDto) {
        //合唱逻辑
        String roomUuid = param.getRoomUuid();
        String chorusId = param.getChorusId();
        ChorusRecord chorusRecord = chorusRecordService.getChorusRecordByChorusId(chorusId);
        if (ChorusStateEnum.CHORUS_READY.getCode() != chorusRecord.getState()) {
            throw new BsException(ErrorCode.CHORUS_NOT_READY);
        }
        OrderSong orderSong = orderSongService.getAndCheckKtvOrderSong(operator, chorusRecord.getOrderId());

        //取消播放下一首歌曲超时任务
        delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));

        // 更新合唱状态
        ChorusRecord newChorusRecord = chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_ING.getCode());

        // 点歌台状态歌曲标记为playing
        changeOrderSongState(orderSong, OrderSongStatusEnum.PLAYING.getCode());

        //设置当前的演唱信息
        singInfo.setChorusId(newChorusRecord.getChorusId());
        singInfo.setSongStatus(KtvSingStatusEnum.PLAY.getStatus());
        singInfo.setExt(param.getExt());
        singInfo.setAssistantUuid(newChorusRecord.getAssistantUuid());
        singRedisWrapper.setSingBaseInfo(roomUuid, singInfo);

        // 发送开始演唱消息
        SingDetailInfoDto singDetailInfoDto = buildSingDetailInfo(singInfo, liveRecordDto);
        messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.START_SING.getType()));
    }

    private void startSoloSing(String operator, SingBaseInfoDto singInfo, Map<String, Object> ext, LiveRecordDto liveRecordDto) {
        String roomUuid = singInfo.getRoomUuid();
        Long orderId = singInfo.getOrderId();
        OrderSong orderSong = orderSongService.getAndCheckKtvOrderSong(operator, orderId);

        //取消播放下一首歌曲超时任务
        delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));

        //点歌台状态歌曲标记为playing
        changeOrderSongState(orderSong, OrderSongStatusEnum.PLAYING.getCode());

        //设置当前的演唱信息
        singInfo.setExt(ext);
        singInfo.setSongStatus(KtvSingStatusEnum.PLAY.getStatus());
        singRedisWrapper.setSingBaseInfo(roomUuid, singInfo);

        //发送开始演唱消息
        SingDetailInfoDto singDetailInfoDto = buildSingDetailInfo(singInfo, liveRecordDto);
        messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.START_SING.getType()));
    }

    private void changeOrderSongState(OrderSong orderSong, int state) {
        //变更点歌的状态信息
        orderSong.setStatus(state);
        orderSong.setUpdateTime(new Date());

        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);
    }

    @Override
    public void singControl(String operator, SingActionParam singActionParam) {
        String roomUuid = singActionParam.getRoomUuid();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);

        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null) {
            //兼容客户端实时合唱时主副唱均会调用结束演唱导致出现错误提示语
            //后续统一修改为当无演唱信息并且调用结束演唱接口，一律走放弃歌曲逻辑（客户端与服务端均需要修改）
            if (singActionParam.getAction() != null && singActionParam.getAction() == 2) {
                return;
            }
            throw new BsException(ErrorCode.SING_INFO_EMPTY);
        }

        //判断操作人是否为主唱或者副唱或者房主
        String userUuid = singBaseInfo.getUserUuid();
        String assistantUuid = singBaseInfo.getAssistantUuid();
        if (StringUtils.isEmpty(operator) || (!operator.equals(userUuid) && !operator.equals(liveRecordDto.getUserUuid())
                && !operator.equals(assistantUuid))) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }

        KtvActionEnum ktvActionEnum = KtvActionEnum.fromAction(singActionParam.getAction());
        if (ktvActionEnum == null) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }
        switch (ktvActionEnum) {
            case PAUSE:
                Integer pauseStatus = singBaseInfo.getSongStatus();
                if (pauseStatus != null && KtvSingStatusEnum.PAUSE.getStatus() == pauseStatus) {
                    throw new BsException(ErrorCode.SING_ALREADY_PAUSE);
                }
                singBaseInfo.setSongStatus(KtvSingStatusEnum.PAUSE.getStatus());
                singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

                //发送暂停演唱消息
                SingDetailInfoDto singDetailInfoDto = buildSingDetailInfo(singBaseInfo, liveRecordDto);
                messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.PAUSE_SING.getType()));

                break;
            case PLAY:
                Integer playStatus = singBaseInfo.getSongStatus();
                if (playStatus != null && KtvSingStatusEnum.PLAY.getStatus() == playStatus) {
                    throw new BsException(ErrorCode.SING_ALREADY_PLAY);
                }
                singBaseInfo.setSongStatus(KtvSingStatusEnum.PLAY.getStatus());
                singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

                //发送继续演唱消息
                SingDetailInfoDto detailInfoDto = buildSingDetailInfo(singBaseInfo, liveRecordDto);
                messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(buildSingBroadcast(detailInfoDto, operator), EventTypeEnum.CONTINUE_SING.getType()));
                break;
            case END:
                Integer endStatus = singBaseInfo.getSongStatus();
                if (endStatus != null && KtvSingStatusEnum.END.getStatus() == endStatus) {
                    throw new BsException(ErrorCode.SING_ALREADY_END);
                }
                singBaseInfo.setSongStatus(KtvSingStatusEnum.END.getStatus());
                String chorusId = singBaseInfo.getChorusId();
                if (StringUtils.isNotEmpty(chorusId)) {
                    ChorusRecord chorusRecord = chorusRecordService.getChorusRecordByChorusId(chorusId);
                    chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_END.getCode());
                }
                endSingHandler(roomUuid, liveRecordDto, singBaseInfo, operator);
                break;
            default:
                break;
        }
    }

    private void endSingHandler(String roomUuid, LiveRecordDto liveRecordDto, SingBaseInfoDto singBaseInfo, String operator) {
        //删除演唱/合唱信息
        singRedisWrapper.delSingBaseInfo(roomUuid);

        //当前点歌歌曲置成已唱
        Long orderId = singBaseInfo.getOrderId();
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId == null ? -1 : orderId);
        if (orderSong != null) {
            orderSong.setStatus(OrderSongStatusEnum.PLAYED.getCode());
            orderSong.setUpdateTime(new Date());
            orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);
        }

        SingDetailInfoDto singDetailInfoDto = buildSingDetailInfo(singBaseInfo, liveRecordDto);

        //广播结束消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.END_SING.getType()));

        //广播点歌列表变化
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(null, EventTypeEnum.ENT_ORDER_SONG_LIST_CHANGE.getType()));

        // 广播下一首歌曲消息
        broadcastPlayNextSong(operator, liveRecordDto.getId());
    }

    @Override
    public SingDetailInfoDto getSingInfo(String roomUuid) {
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null) {
            //无当前房间演唱信息时拿取未开始的演唱信息
            return new SingDetailInfoDto();
        }
        return buildSingDetailInfo(singBaseInfo, liveRecordDto);
    }

    @Override
    public void destroySingInfo(String operator, String roomUuid) {
        if (StringUtils.isAnyEmpty(roomUuid)) {
            return;
        }
        singRedisWrapper.delSingBaseInfo(roomUuid);
        //当前房间内未结束的合唱记录置成已结束
        List<ChorusRecord> chorusRecords = chorusRecordService.getNotEndChorusRecord(roomUuid);
        if (chorusRecords != null && !chorusRecords.isEmpty()) {
            for (ChorusRecord chorusRecord : chorusRecords) {
                chorusRecord.setStatus(StatusEnum.INVALID.getCode());
                chorusRecord.setState(ChorusStateEnum.CHORUS_END.getCode());
                chorusRecordMapperWrapper.updateByPrimaryKeySelective(chorusRecord);
            }
        }
    }

    @Override
    public List<Object> getSingList(String roomUuid) {
        return null;
    }

    @Override
    public void endSingWhenMemberOut(String roomUuid, String userUuid, List<OrderSongResultDto> oldOrderSongs) {
        if (StringUtils.isAnyEmpty(roomUuid)) {
            return;
        }
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null ||
                (!userUuid.equals(singBaseInfo.getUserUuid()) && !userUuid.equals(singBaseInfo.getAssistantUuid()))) {
            // 无演唱信息时退出人员是点歌列表第一首歌的点歌人，直接播放下一首
            if (singBaseInfo == null && oldOrderSongs != null && !oldOrderSongs.isEmpty()) {
                OrderSongResultDto orderSongResultDto = oldOrderSongs.get(0);
                OrderSongDto orderSongDto = orderSongResultDto.getOrderSong();
                if (userUuid.equals(orderSongDto.getUserUuid())) {
                    OrderSong orderSong = buildOrderSong(orderSongResultDto);
                    if (orderSong != null) {
                        //取消播放下一首歌曲超时任务
                        delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));
                    }

                    SingDetailInfoDto singDetailInfoDto = buildAbandonSingMsg(userUuid, orderSong, liveRecordDto);
                    //广播放弃歌曲消息
                    messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, userUuid), EventTypeEnum.ABANDON_SING.getType()));


                    // 广播下一首歌曲消息
                    broadcastPlayNextSong(userUuid, liveRecordDto.getId());
                }
            }
        } else {
            //退出人员是正在演唱人员处理逻辑
            endChorus(singBaseInfo);

            //取消播放下一首歌曲超时任务
            Long orderId = singBaseInfo.getOrderId();
            if (orderId != null && oldOrderSongs != null && !oldOrderSongs.isEmpty()) {
                OrderSong orderSong = buildOrderSong(oldOrderSongs.stream().filter(
                        oldOrderSong -> orderId.equals(oldOrderSong.getOrderSong().getId())).findFirst().orElse(null));
                if (orderSong != null) {
                    //取消播放下一首歌曲超时任务
                    delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));
                }
            }

            endSingHandler(roomUuid, liveRecordDto, singBaseInfo, userUuid);
        }
    }

    @Override
    public void endSingWhenSongSwitch(String roomUuid, String operator, Long orderId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId == null ? -1 : orderId);
        if (orderSong != null) {
            //取消播放下一首歌曲超时任务
            delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));
        }

        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo != null) {
            singRedisWrapper.delSingBaseInfo(roomUuid);
            endChorus(singBaseInfo);
        }

        SingDetailInfoDto singDetailInfoDto = buildSingDetailInfo(singBaseInfo, liveRecordDto);

        //发送结束消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.END_SING.getType()));

        //发送播放下一首歌曲消息
        broadcastPlayNextSong(operator, liveRecordDto.getId());
    }

    private void endChorus(SingBaseInfoDto singBaseInfo) {
        if(singBaseInfo == null) {
            return;
        }
        //变更合唱状态
        String chorusId = singBaseInfo.getChorusId();
        if (StringUtils.isNotEmpty(chorusId)) {
            ChorusRecord chorusRecord = chorusRecordService.getByChorusId(chorusId);
            if (chorusRecord != null) {
                chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_END.getCode());
            }
        }
    }

    @Override
    public void abandonSing(String operator, AbandonSingParam param) {
        String roomUuid = param.getRoomUuid();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);

        Long orderId = param.getOrderId();
        OrderSong orderSong = orderSongService.getAndCheckKtvOrderSong(operator, orderId);

        orderSong.setStatus(OrderSongStatusEnum.PLAYED.getCode());
        orderSong.setUpdateTime(new Date());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);

        // 取消播放下一首歌曲超时任务
        PlayNextSongTask task = new PlayNextSongTask(orderSong);
        delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(task));

        // 兼容中途放弃演唱的场景
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo != null) {
            singRedisWrapper.delSingBaseInfo(roomUuid);
        }

        SingDetailInfoDto singDetailInfoDto = buildAbandonSingMsg(operator, orderSong, liveRecordDto);

        //广播放弃消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, operator), EventTypeEnum.ABANDON_SING.getType()));

        //广播点歌列表变化
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(null, EventTypeEnum.ENT_ORDER_SONG_LIST_CHANGE.getType()));

        // 广播下一首歌曲消息
        broadcastPlayNextSong(operator, liveRecordDto.getId());
    }

    @Override
    public void playNextSongMsg(String operator, String roomUuid, OrderSongResultDto resultDto) {
        SingDetailInfoDto detailInfo = new SingDetailInfoDto();
        detailInfo.setRoomUuid(roomUuid);
        detailInfo.setOrderSongInfo(resultDto);

        OrderSongDto orderSongDto = resultDto.getOrderSong();

        //设置下一首歌曲为唱歌中（避免切歌与取消的并发问题）
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderSongDto.getId());
        if (orderSong != null) {
            orderSong.setStatus(OrderSongStatusEnum.PLAYING.getCode());
            orderSong.setUpdateTime(new Date());
            orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);
        }

        // 设置房间演唱前信息
        singRedisWrapper.setSingBaseInfo(roomUuid, new SingBaseInfoDto(orderSongDto.getUserUuid(),
                null, orderSongDto.getId(), roomUuid, KtvSingStatusEnum.NOT_SING.getStatus(),
                null, new HashMap<>()));

        // 发送开始播放下一首歌曲消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(detailInfo, operator), EventTypeEnum.PLAY_NEXT_SONG.getType()));


        //提交超时任务，当播放下一首消息发出后20s内未收到开始演唱或结束演唱或放弃演唱则视为放弃该歌曲
        PlayNextSongTask task = new PlayNextSongTask(orderSong);
        delayQueueProducer.send(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(task), 20000);
    }

    @Override
    public void joinChorusTimeOut(String roomUuid, String chorusId, Long orderId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);

        ChorusRecord chorusRecord = chorusRecordService.getByChorusId(chorusId == null ? "-1" : chorusId);
        if (chorusRecord != null) {
            chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_END.getCode());
        }

        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId == null ? -1 : orderId);
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSongForKtv(orderSong.getStatus())) {
            return;
        }

        orderSong.setStatus(OrderSongStatusEnum.PLAYED.getCode());
        orderSong.setUpdateTime(new Date());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);

        //取消播放下一首歌曲超时任务
        delayQueueProducer.cancel(DelayTopic.PLAY_NEXT_SONG_TOPIC, GsonUtil.toJson(new PlayNextSongTask(orderSong)));

        String assistantUuid = chorusRecord == null ? null : chorusRecord.getAssistantUuid();
        SingDetailInfoDto singDetailInfoDto = buildAbandonSingMsg(assistantUuid, orderSong, liveRecordDto);

        //广播放弃消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, assistantUuid), EventTypeEnum.ABANDON_SING.getType()));

        // 广播点歌列表变化
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(null, EventTypeEnum.ENT_ORDER_SONG_LIST_CHANGE.getType()));

        // 广播下一首歌曲消息
        broadcastPlayNextSong(assistantUuid, liveRecordDto.getId());
    }

    @Override
    public void playNextSongTimeOut(String roomUuid, Long orderId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSongForKtv(orderSong.getStatus())) {
            return;
        }
        orderSong.setStatus(OrderSongStatusEnum.PLAYED.getCode());
        orderSong.setUpdateTime(new Date());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);

        //兼容中途放弃演唱的场景
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo != null && singBaseInfo.getOrderId().equals(orderId)) {
            singRedisWrapper.delSingBaseInfo(roomUuid);
        }

        String userUuid = orderSong.getUserUuid();
        SingDetailInfoDto singDetailInfoDto = buildAbandonSingMsg(userUuid, orderSong, liveRecordDto);

        //广播放弃消息
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(buildSingBroadcast(singDetailInfoDto, userUuid), EventTypeEnum.ABANDON_SING.getType()));

        //广播点歌列表变化
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(null, EventTypeEnum.ENT_ORDER_SONG_LIST_CHANGE.getType()));

        // 广播下一首歌曲消息
        broadcastPlayNextSong(userUuid, liveRecordDto.getId());
    }

    @Override
    public void cleanSingInfo(String roomUuid) {
        singRedisWrapper.delSingBaseInfo(roomUuid);
    }

    private SingDetailInfoDto buildSingDetailInfo(SingBaseInfoDto singBaseInfo, LiveRecordDto liveRecordDto) {
        SingDetailInfoDto detailInfo = new SingDetailInfoDto();
        if (singBaseInfo == null) {
            return detailInfo;
        }
        BeanUtils.copyProperties(singBaseInfo, detailInfo);

        // 设置演唱个人信息
        buildSingerInfo(singBaseInfo, detailInfo);

        // 设置副唱个人信息
        buildAssistantInfo(singBaseInfo, detailInfo);

        //歌曲信息
        buildSongInfo(singBaseInfo, detailInfo);

        //演唱模式
        buildChorusInfo(singBaseInfo, liveRecordDto, detailInfo);

        return detailInfo;
    }

    private void buildChorusInfo(SingBaseInfoDto singBaseInfo, LiveRecordDto liveRecordDto, SingDetailInfoDto detailInfo) {
        detailInfo.setSingMode(liveRecordDto.getSingMode());
        //合唱类型（串行与实时）
        String chorusId = singBaseInfo.getChorusId();
        if (StringUtils.isNotEmpty(chorusId)) {
            ChorusRecord chorusRecord = chorusRecordService.getByChorusId(chorusId);
            if (chorusRecord != null) {
                detailInfo.setChorusType(chorusService.judgeChorusType(chorusRecord));
                detailInfo.setChorusStatus(chorusRecord.getState());
            }
        }
    }

    private void buildSongInfo(SingBaseInfoDto singBaseInfo, SingDetailInfoDto detailInfo) {
        Long orderId = singBaseInfo.getOrderId();
        if (orderId != null) {
            OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
            if (orderSong != null) {
                detailInfo.setOrderSongInfo(orderSong);
            }
        }
    }

    private void buildAssistantInfo(SingBaseInfoDto singBaseInfo, SingDetailInfoDto detailInfo) {
        String assistantUuid = singBaseInfo.getAssistantUuid();
        if (StringUtils.isNotEmpty(assistantUuid)) {
            UserDto userBasicInfo = userService.getUser(assistantUuid);
            if (userBasicInfo != null) {
                detailInfo.setAssistantName(userBasicInfo.getUserName());
                detailInfo.setAssistantIcon(userBasicInfo.getIcon());
            }
        }
    }

    private void buildSingerInfo(SingBaseInfoDto singBaseInfo, SingDetailInfoDto detailInfo) {
        String userUuid = singBaseInfo.getUserUuid();
        if (StringUtils.isNotEmpty(userUuid)) {
            UserDto userBasicInfo = userService.getUser(userUuid);
            if (userBasicInfo != null) {
                detailInfo.setUserName(userBasicInfo.getUserName());
                detailInfo.setIcon(userBasicInfo.getIcon());
            }
        }
    }

    private SingDetailInfoDto buildAbandonSingMsg(String operator, OrderSong orderSong, LiveRecordDto recordDto) {
        SingDetailInfoDto detailInfo = new SingDetailInfoDto();
        detailInfo.setRoomUuid(recordDto.getRoomUuid());
        detailInfo.setOrderSongInfo(orderSong);

        if (StringUtils.isNotEmpty(operator)) {
            detailInfo.setUserUuid(operator);
            UserDto userBasicInfo = userService.getUser(operator);
            if (userBasicInfo != null) {
                detailInfo.setUserName(userBasicInfo.getUserName());
                detailInfo.setIcon(userBasicInfo.getIcon());
            }
        }
        detailInfo.setSingMode(recordDto.getSingMode());
        return detailInfo;
    }

    private SingBroadcastResultDto buildSingBroadcast(SingDetailInfoDto singDetailInfoDto, String operatorUuid) {
        if (singDetailInfoDto == null) {
            return null;
        }
        SingBroadcastResultDto singBroadcastResultDto = new SingBroadcastResultDto();
        if (StringUtils.isNotEmpty(operatorUuid)) {
            UserDto userBasicInfo = userService.getUser(operatorUuid);
            singBroadcastResultDto.setOperator(BasicUserDto.buildBasicUser(userBasicInfo));
        }
        singBroadcastResultDto.setSingInfo(singDetailInfoDto);
        return singBroadcastResultDto;
    }

    private void broadcastPlayNextSong(String operator, Long liveRecordId) {
        List<OrderSongResultDto> orderSongs = orderSongService.getOrderSongs(liveRecordId);
        if (orderSongs == null) {
            orderSongs = Collections.emptyList();
        }
        if (!orderSongs.isEmpty()) {
            for (OrderSongResultDto resultDto : orderSongs) {
                OrderSongDto orderSongDto = resultDto.getOrderSong();
                if (orderSongDto.getStatus() != null && OrderSongStatusEnum.WAITING.getCode() == orderSongDto.getStatus()) {
                    playNextSongMsg(operator, orderSongDto.getRoomUuid(), resultDto);
                    break;
                }
            }
        }
    }

    private OrderSong buildOrderSong(OrderSongResultDto orderSongResultDto) {
        if (orderSongResultDto == null) {
            return null;
        }
        OrderSong orderSong = new OrderSong();
        BeanUtils.copyProperties(orderSongResultDto.getOrderSong(), orderSong);
        orderSong.setId(orderSongResultDto.getOrderSong().getId());
        return orderSong;
    }
}
