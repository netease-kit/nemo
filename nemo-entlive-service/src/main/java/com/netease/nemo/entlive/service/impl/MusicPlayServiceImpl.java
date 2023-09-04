package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.ListenTogetherPlayTask;
import com.netease.nemo.entlive.dto.BasicUserDto;
import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.dto.PlayDetailInfoDto;
import com.netease.nemo.entlive.dto.message.MusicPlayNotifyEventDto;
import com.netease.nemo.entlive.enums.*;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.entlive.parameter.MusicActionParam;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.entlive.service.LiveRecordService;
import com.netease.nemo.entlive.service.MusicPlayService;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import com.netease.nemo.entlive.wrapper.MusicPlayerRedisWrapper;
import com.netease.nemo.entlive.wrapper.OrderSongMapperWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.paramters.neroom.NeRoomMessageParam;
import com.netease.nemo.queue.producer.DelayQueueProducer;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.netease.nemo.entlive.enums.MusicPlayerActionEnum.getEventType;

/**
 * 歌曲播放服务
 *
 * @Author：CH
 * @Date：2023/5/31 4:30 下午
 */
@Service
@Slf4j
public class MusicPlayServiceImpl implements MusicPlayService {

    @Resource
    private UserService userService;

    @Resource
    private DelayQueueProducer delayQueueProducer;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private OrderSongMapperWrapper orderSongMapperWrapper;

    @Resource
    private MusicPlayerRedisWrapper musicPlayerRedisWrapper;

    @Override
    public PlayDetailInfoDto getPlayMusicInfo(Long liveRecordId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }

        return musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
    }

    @Override
    public void musicReady(Long liveRecordId, Long orderId) {
        String userUuid = Context.get().getUserUuid();

        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }

        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSong(orderSong.getStatus())) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }

        if(!liveRecordId.equals(orderSong.getLiveRecordId())){
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }

        Integer liveType = liveRecordDto.getLiveType();
        if (LiveTypeEnum.CHAT.getType() == liveType) {
            if (!(orderSong.getUserUuid().equals(userUuid) || liveRecordDto.getUserUuid().equals(userUuid))) {
                throw new BsException(ErrorCode.FORBIDDEN);
            }
            // 语聊房歌曲ready时直接播放歌曲
            musicPlay(liveRecordId, Context.get().getUserUuid(), orderSong);
        } else if (LiveTypeEnum.LISTEN_TOGETHER.getType() == liveType) {
            // 一起听房歌曲ready时发送消息通知主播
            handlerListenMusicPlay(liveRecordDto, Context.get().getUserUuid(), orderSong);
        }
    }

    private void handlerListenMusicPlay(LiveRecordDto liveRecordDto, String userUuid, OrderSong orderSong) {
        Long liveRecordId = liveRecordDto.getId();
        Long orderId = orderSong.getId();

        List<RoomMember> members = neRoomMemberService.getRoomMembers(liveRecordDto.getRoomArchiveId());
        if (members.size() < 2) {
            musicPlay(liveRecordId, userUuid, orderSong);
        } else {
            // 用户设置歌曲ready
            musicPlayerRedisWrapper.musicReady(liveRecordId, orderId, userUuid);

            ListenTogetherPlayTask task = ListenTogetherPlayTask.builder().liveRecordId(liveRecordId).orderId(orderSong.getId()).build();

            // 判断是否所有人都已准备
            boolean allReady = isMusicReady(liveRecordId, orderSong.getId(), members.stream().map(RoomMember::getUserUuid).collect(Collectors.toList()));

            if (!allReady) {
                // 获取房间歌曲播放信息
                PlayDetailInfoDto playDetailInfoDto = musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
                if (playDetailInfoDto != null && playDetailInfoDto.getOrderId().equals(orderSong.getId())) {
                    playDetailInfoDto.setMusicStatus(MusicPlayerStatusEnum.READYING.getStatus());
                } else {
                    playDetailInfoDto = new PlayDetailInfoDto(orderSong, MusicPlayerStatusEnum.READYING);
                }
                musicPlayerRedisWrapper.putMusicPlayerInfo(liveRecordId, playDetailInfoDto);

                // 发送消息观众或者主播歌曲已准备消息到房间
                MusicPlayNotifyEventDto musicPlayNotifyEventDto = new MusicPlayNotifyEventDto(getOperatorInfoDto(userUuid));
                musicPlayNotifyEventDto.setPlayMusicInfo(playDetailInfoDto);
                sendMusicPlayerMessage(musicPlayNotifyEventDto, playDetailInfoDto.getRoomUuid(), EventTypeEnum.ENT_MUSIC_READY.getType());

                // 发送延迟消息，30s后检查歌曲是否已准备，如果未准备则直接播放歌曲
                delayQueueProducer.send(DelayTopic.LISTEN_TOGETHER_TOPIC, GsonUtil.toJson(task), 30000);
            } else {
                // 取消延迟消息
                delayQueueProducer.cancel(DelayTopic.LISTEN_TOGETHER_TOPIC, GsonUtil.toJson(task));
                musicPlay(liveRecordId, userUuid, orderSong);
            }
        }
    }

    private boolean isMusicReady(Long liveRecordId, Long orderId, List<String> userUuids) {
        boolean allMusicReady = false;
        if (CollectionUtils.isEmpty(userUuids)) {
            return false;
        }
        for (String userUuid : userUuids) {
            allMusicReady = musicPlayerRedisWrapper.isMusicReady(liveRecordId, orderId, userUuid);
            if (!allMusicReady) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void musicAction(String userUuid, MusicActionParam param) {
        Long liveRecordId = param.getLiveRecordId();
        Integer action = param.getAction();

        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }

        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        PlayDetailInfoDto playDetailInfoDto = musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
        if (playDetailInfoDto == null) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }

        playDetailInfoDto.setMusicStatus(transferMusicPlayStatus(param.getAction()));
        musicPlayerRedisWrapper.putMusicPlayerInfo(liveRecordId, playDetailInfoDto);

        // 开始播放是设置点歌台状态
        if (MusicPlayerStatusEnum.PLAY.getStatus() == param.getAction()) {
            setSongPlaying(playDetailInfoDto.getOrderId());
            // 重置其他点歌歌曲的状态
            resetOtherOrderSongStatus(liveRecordId, playDetailInfoDto.getOrderId());
        }

        MusicPlayNotifyEventDto musicPlayNotifyEventDto = new MusicPlayNotifyEventDto(getOperatorInfoDto(userUuid));
        musicPlayNotifyEventDto.setPlayMusicInfo(playDetailInfoDto);

        sendMusicPlayerMessage(musicPlayNotifyEventDto, playDetailInfoDto.getRoomUuid(), getEventType(action, param.getFirstPlay()).getType());
    }

    /**
     * action 转 music的status
     *
     * @param action 操作类型
     * @return 歌曲状态
     */
    private Integer transferMusicPlayStatus(Integer action) {
        MusicPlayerActionEnum actionEnum = MusicPlayerActionEnum.fromAction(action);
        switch (actionEnum) {
            case PLAY:
                return MusicPlayerStatusEnum.PLAY.getStatus();
            case PAUSE:
                return MusicPlayerStatusEnum.PAUSE.getStatus();
            default:
                break;
        }
        return null;
    }

    @Override
    public void cleanPlayerMusicInfo(Long liveRecordId, Long orderId) {
        PlayDetailInfoDto playDetailInfoDto = musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
        if (playDetailInfoDto != null && orderId.equals(playDetailInfoDto.getOrderId())) {
            musicPlayerRedisWrapper.deleteMusicPlayerInfo(liveRecordId);
        }
        musicPlayerRedisWrapper.deleteMusicReady(liveRecordId, orderId);
    }

    @Override
    public void cleanPlayerMusicInfo(Long liveRecordId) {
        PlayDetailInfoDto playDetailInfoDto = musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
        if (playDetailInfoDto != null) {
            Long orderId = playDetailInfoDto.getOrderId();
            musicPlayerRedisWrapper.deleteMusicReady(liveRecordId, orderId);
            musicPlayerRedisWrapper.deleteMusicPlayerInfo(liveRecordId);
        }
    }

    /**
     * 开始播放歌曲
     *
     * @param liveRecordId 直播间唯一记录编号
     * @param userUuid     主播账号
     * @param orderSong    点歌信息
     */
    private void musicPlay(Long liveRecordId, String userUuid, OrderSong orderSong) {
        MusicPlayNotifyEventDto musicPlayNotifyEventDto = new MusicPlayNotifyEventDto(getOperatorInfoDto(userUuid));
        PlayDetailInfoDto playDetailInfoDto = new PlayDetailInfoDto(orderSong, MusicPlayerStatusEnum.PLAY);
        musicPlayNotifyEventDto.setPlayMusicInfo(playDetailInfoDto);

        // 缓存歌曲播放信息
        musicPlayerRedisWrapper.putMusicPlayerInfo(liveRecordId, playDetailInfoDto);

        // 设置点歌台歌曲状态
        setSongPlaying(orderSong);
        // 重置其他点歌歌曲的状态
        resetOtherOrderSongStatus(liveRecordId, orderSong.getId());

        // 发送歌曲播放的消息
        sendMusicPlayerMessage(musicPlayNotifyEventDto, playDetailInfoDto.getRoomUuid(), EventTypeEnum.ENT_MUSIC_PLAY.getType());
    }

    /**
     * 发送歌曲播放信息消息到NeRoom
     *
     * @param musicPlayNotifyEventDto 歌曲播放通知对象
     * @param roomUuid                房间uuid
     * @param type                    消息事件
     */
    private void sendMusicPlayerMessage(MusicPlayNotifyEventDto musicPlayNotifyEventDto, String roomUuid, Integer type) {
        EventDto eventDto = new EventDto(musicPlayNotifyEventDto, type);
        NeRoomMessageParam neRoomMessageParam = NeRoomMessageParam
                .builder()
                .roomUuid(roomUuid)
                .message(GsonUtil.toJson(eventDto))
                .build();
        neRoomService.sendNeRoomCustomMessage(neRoomMessageParam);
    }

    private void setSongPlaying(Long orderId) {
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(orderId);
        setSongPlaying(orderSong);
    }

    private void setSongPlaying(OrderSong orderSong) {
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSong(orderSong.getStatus())) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }
        if (OrderSongStatusEnum.PLAYING.getCode() == orderSong.getStatus()) {
            return;
        }
        orderSong.setStatus(OrderSongStatusEnum.PLAYING.getCode());
        orderSongMapperWrapper.updateByPrimaryKeySelective(orderSong);
    }

    /**
     * 重置其他歌曲的状态
     *
     * @param liveRecordId   直播唯一记录
     * @param playingOrderId 当前播放的点歌ID
     */
    private void resetOtherOrderSongStatus(Long liveRecordId, Long playingOrderId) {
        List<OrderSong> orderSongs = orderSongMapperWrapper.selectByLiveRecordId(liveRecordId);
        if (!CollectionUtils.isEmpty(orderSongs)) {
            orderSongs.stream()
                    .filter(o -> (o.getStatus() == OrderSongStatusEnum.PLAYING.getCode() && !o.getId().equals(playingOrderId)))
                    .forEach(o -> {
                        o.setStatus(OrderSongStatusEnum.WAITING.getCode());
                        orderSongMapperWrapper.updateByPrimaryKeySelective(o);
                    });
        }
    }

    private BasicUserDto getOperatorInfoDto(String userUuid) {
        UserDto userDto = userService.getUser(userUuid);
        return BasicUserDto.buildBasicUser(userDto);
    }
}
