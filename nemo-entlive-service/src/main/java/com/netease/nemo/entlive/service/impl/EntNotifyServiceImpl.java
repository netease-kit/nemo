package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.parameter.neroomNotify.*;
import com.netease.nemo.entlive.service.EntLiveService;
import com.netease.nemo.entlive.service.EntNotifyService;
import com.netease.nemo.entlive.service.LiveRecordService;
import com.netease.nemo.entlive.service.OrderSongService;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.locker.LockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.netease.nemo.enums.RedisKeyEnum.NE_ROOM_MEMBER_TABLE_KEY;

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


    @Override
    public void handlerCreateRoom(CreateRoomEventNotify param) {
        //TODO
    }

    @Override
    public void handlerCloseRoom(CloseRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if(liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        Long liveRecordId = liveRecordDto.getId();

        lockerService.tryLockAndDo(
                () -> liveRecordService.updateLiveState(liveRecordId, LiveEnum.LIVE_CLOSE.getCode()),
                RedisKeyEnum.ENT_CREATE_LIVE_ROOM_LOCK_KEY, liveRecordId);
    }

    @Override
    public void handlerUserJoinRoom(JoinRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if(liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        Long liveRecordId = liveRecordDto.getId();

        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Set<RoomMember> users = param.getUsers();
        users.forEach(o -> {
            nemoRedisTemplate.opsForHash().put(neRoomMemberTableKey, o.getUserUuid(), o);

            // 如果用户是主播且直播间状态是'待直播'，则标记直播状态为为'直播中'
            if (o.getUserUuid().equals(liveRecordDto.getUserUuid())
                    && liveRecordDto.getLive().equals(LiveEnum.NOT_START.getCode())) {
                lockerService.tryLockAndDo(
                        () -> liveRecordService.updateLiveState(liveRecordId, LiveEnum.LIVE.getCode()),
                        RedisKeyEnum.ENT_CREATE_LIVE_ROOM_LOCK_KEY, liveRecordId);
            }
        });
        nemoRedisTemplate.expire(neRoomMemberTableKey, 7, TimeUnit.DAYS);
    }

    @Override
    public void handlerUserLeaveRoom(LeaveRoomEventNotify param) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecordByRoomArchiveId(param.getRoomArchiveId());
        if(liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }
        Long liveRecordId = liveRecordDto.getId();

        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + param.getRoomArchiveId();
        Set<RoomMember> users = param.getUsers();
        users.forEach(o -> {
            nemoRedisTemplate.opsForHash().delete(neRoomMemberTableKey, o.getUserUuid(), o);

            // 如果用户是主播则结束直播
            if (liveRecordDto.getUserUuid().equals(o.getUserUuid())) {
                lockerService.tryLockAndDo(
                        () -> entLiveService.closeLiveRoom(o.getUserUuid(), liveRecordId),
                        RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, liveRecordId);
            }
            // 删除用户点歌
//            lockerService.tryLockAndDo(
//                    () -> orderSongService.cleanUserOrderSongs(liveRecordId, o.getUserUuid()),
//                    RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), liveRecordId);
        });
    }
}
