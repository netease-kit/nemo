package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.dto.OrderSongResultDto;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.event.GameRoomCloseEvent;
import com.netease.nemo.entlive.event.GameUserLeaveRoomEvent;
import com.netease.nemo.entlive.parameter.neroomNotify.*;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.openApi.dto.neroom.UserOffSeatNotifyDto;
import com.netease.nemo.openApi.dto.neroom.UserOnSeatNotifyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.netease.nemo.enums.RedisKeyEnum.*;

@Service
@Slf4j
public class EntNotifyServiceImpl implements EntNotifyService {

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private EntLiveService entLiveService;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private OrderSongService orderSongService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Resource
    private MusicPlayService musicPlayService;

    @Resource
    private SingService singService;

    @Resource
    private ApplicationEventPublisher publisher;


    @Override
    public void handlerCreateRoom(CreateRoomEventNotify param) {
        //TODO
    }

    @Override
    public void handlerCloseRoom(CloseRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        if(liveRecordDto.getLive().equals(LiveEnum.LIVE_CLOSE.getCode())){
            log.info("LiveRecord is closed");
            return;
        }

        Long liveRecordId = liveRecordDto.getId();
        lockerService.tryLockAndDo(
                () -> {
                    // 变更当前直播状态为结束
                    liveRecordService.updateLiveState(liveRecordId, LiveEnum.LIVE_CLOSE.getCode());
                    // 清空当前播放歌曲信息
                    musicPlayService.cleanPlayerMusicInfo(liveRecordId);
                    // 清空点歌数据
                    orderSongService.cleanOrderSongs(liveRecordId);

                    // 清空房间演唱信息
                    if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
                        singService.cleanSingInfo(liveRecordDto.getRoomUuid());
                    }

                    // 游戏房发送房间结束通知
                    if (LiveTypeEnum.isGameLive(liveRecordDto.getLiveType())) {
                        GameRoomCloseEvent gameRoomCloseTask = new GameRoomCloseEvent(liveRecordDto.getRoomUuid());
                        publisher.publishEvent(gameRoomCloseTask);
                    }
                },
                RedisKeyEnum.ENT_CREATE_LIVE_ROOM_LOCK_KEY, liveRecordId);
    }

    @Override
    public void handlerUserJoinRoom(JoinRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        Long liveRecordId = liveRecordDto.getId();

        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Set<RoomMember> users = param.getUsers();
        users.forEach(o -> {
            nemoRedisTemplate.opsForHash().put(neRoomMemberTableKey, o.getUserUuid(), o);

            // 如果用户是主播且直播间状态是'待直播'、`暂停中`，则标记直播状态为为'直播中'
            if (o.getUserUuid().equals(liveRecordDto.getUserUuid())
                    && (LiveEnum.isNotStart(liveRecordDto.getLive()) || LiveEnum.isPause(liveRecordDto.getLive()))) {
                lockerService.tryLockAndDo(
                        () -> {
                            if(!LiveTypeEnum.isPkLive(liveRecordDto.getLiveType())) {
                                liveRecordService.updateLiveState(liveRecordId, LiveEnum.LIVE.getCode());
                            }
                            else{
                                entLiveService.updatePkLiveLayout(liveRecordId);
                            }
                        },
                        ENT_LIVE_ROOM_LOCK_KEY, liveRecordId);
            }
        });
        nemoRedisTemplate.expire(neRoomMemberTableKey, 7, TimeUnit.DAYS);
    }

    @Override
    public void handlerUserLeaveRoom(LeaveRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        Long liveRecordId = liveRecordDto.getId();

        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Set<RoomMember> users = param.getUsers();
        users.forEach(o -> {
            nemoRedisTemplate.opsForHash().delete(neRoomMemberTableKey, o.getUserUuid(), o);

            if (liveRecordDto.getUserUuid().equals(o.getUserUuid())) {
                // 如果非直播，则关闭直播间
                if (!LiveTypeEnum.isPkLive(liveRecordDto.getLiveType())) {
                    lockerService.tryLockAndDo(
                            () -> entLiveService.closeLiveRoom(o.getUserUuid(), liveRecordId),
                            RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, liveRecordId);
                }
            }

            // KTV场景下删除用户点歌
            if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
                lockerService.tryLockAndDo(
                        () -> {
                            List<OrderSongResultDto> oldOrderSongs = orderSongService.getOrderSongs(liveRecordId);
                            orderSongService.cleanUserOrderSongs(liveRecordId, o.getUserUuid());
                            singService.endSingWhenMemberOut(liveRecordDto.getRoomUuid(), o.getUserUuid(), oldOrderSongs);
                        },
                        RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), liveRecordId);
            }

            if (LiveTypeEnum.isGameLive(liveRecordDto.getLiveType())) {
                GameUserLeaveRoomEvent gameUserLeaveRoomTask = GameUserLeaveRoomEvent.builder().userUuid(o.getUserUuid()).roomUuid(param.getRoomUuid()).build();
                // 通知游戏用户离开房间
                publisher.publishEvent(gameUserLeaveRoomTask);
            }
        });
    }


    @Override
    public void handlerUserOnSeat(UserOnSeatNotifyDto param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }

        String neRoomMemberTableKey = NE_ROOM_SEAT_USER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Integer index = param.getIndex();

        nemoRedisTemplate.opsForHash().put(neRoomMemberTableKey, index, param.getUser());
        // 如果观众上麦，则将直播间状态修改为连麦中
        lockerService.tryLockAndDo(
                () -> {
                    // 更新直播
                    entLiveService.updatePkLiveLayout(liveRecordDto.getId());
                },
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, liveRecordDto.getId());
    }

    @Override
    public void handlerUserOffSeat(UserOffSeatNotifyDto param) {
        String neRoomMemberTableKey = NE_ROOM_SEAT_USER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Integer index = param.getIndex();
        String userUuid = param.getUserUuid();

        // 删除redis中麦位数据
        nemoRedisTemplate.opsForHash().delete(neRoomMemberTableKey, index);

        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }

        Long liveRecordId = liveRecordDto.getId();
        String roomUuid = liveRecordDto.getRoomUuid();
        if (LiveTypeEnum.isPkLive(liveRecordDto.getLive())) {
            //观众主播下麦更新推流布局
            lockerService.tryLockAndDo(() -> {
                entLiveService.updatePkLiveLayout(liveRecordId);
            }, ENT_LIVE_ROOM_LOCK_KEY.getKeyPrefix(), yunXinConfigProperties.getAppKey(), roomUuid);
        }

        if (LiveTypeEnum.isKTVLive(liveRecordDto.getLiveType())) {
            try {
                //ktv场景清空用户已点歌曲
                lockerService.tryLockAndDo(() -> {
                    List<OrderSongResultDto> oldOrderSongs = orderSongService.getOrderSongs(liveRecordId);
                    orderSongService.cleanUserOrderSongs(liveRecordId, userUuid);
                    singService.endSingWhenMemberOut(roomUuid, userUuid, oldOrderSongs);
                }, ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), yunXinConfigProperties.getAppKey(), roomUuid);
            } catch (Exception e) {
                log.info("ktv member offSeat error, roomUuid:{}, userUuid:{}", roomUuid, userUuid);
            }
        }
    }
}
