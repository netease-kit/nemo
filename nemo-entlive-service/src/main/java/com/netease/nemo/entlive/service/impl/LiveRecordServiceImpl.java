package com.netease.nemo.entlive.service.impl;

import com.google.common.collect.Lists;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.entlive.dto.LiveDto;
import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.StatusEnum;
import com.netease.nemo.entlive.mapper.LiveRecordMapper;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.entlive.service.LiveRecordService;
import com.netease.nemo.entlive.wrapper.LiveRecordWrapper;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.neroom.UserOnSeatNotifyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.netease.nemo.enums.RedisKeyEnum.NE_ROOM_SEAT_USER_TABLE_KEY;

/**
 * 娱乐直播房间——直播记录接口实现
 *
 * @Author：CH
 * @Date：2023/5/19 11:11 上午
 */
@Service
@Slf4j
public class LiveRecordServiceImpl implements LiveRecordService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private LiveRecordMapper liveRecordMapper;

    @Resource
    private LiveRecordWrapper liveRecordWrapper;
    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Override
    @Transactional
    public Long addLiveRecord(LiveRecord liveRecord) {
        int res = liveRecordWrapper.insertSelective(liveRecord);
        if (res < 1) {
            log.error("addLiveRecord failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return liveRecord.getId();
    }

    @Override
    public LiveRecordDto getLiveRecord(Long liveRecordId) {
        return Optional.ofNullable(liveRecordWrapper.selectByPrimaryKey(liveRecordId)).map(o -> modelMapper.map(o, LiveRecordDto.class)).orElse(null);
    }

    @Override
    public LiveRecordDto getLivingRecordByUserUuid(String userUuid) {
        return null;
    }

    @Override
    public LiveRecordDto getLivingRecordByRoomArchiveId(String roomArchiveId) {
        LiveRecord liveRecord = liveRecordWrapper.selectByRoomArchiveId(roomArchiveId);
        if(liveRecord == null || !LiveEnum.isLive(liveRecord.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        return modelMapper.map(liveRecord, LiveRecordDto.class);
    }

    @Override
    public LiveRecordDto getLivingRecordByRoomUuid(String roomUuid) {
        LiveRecord liveRecord = liveRecordWrapper.selectByRoomUuid(roomUuid);
        if(liveRecord == null || !LiveEnum.isLive(liveRecord.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        return modelMapper.map(liveRecord, LiveRecordDto.class);
    }

    @Override
    public LiveRecordDto getLiveRecordByUserUuid(String userUuid) {
        if(StringUtils.isEmpty(userUuid)) {
            return null;
        }
        LiveRecord liveRecord = liveRecordWrapper.selectByUserUuid(userUuid);
        if(null == liveRecord) {
            return null;
        }
        return modelMapper.map(liveRecord, LiveRecordDto.class);
    }

    @Override
    public boolean checkAnchorLiving(String userUuid) {
        LiveRecord liveRecord = liveRecordWrapper.selectByUserUuid(userUuid);
        if(null == liveRecord) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        return LiveEnum.isLive(liveRecord.getLive());
    }

    @Override
    public void updateLiveState(Long liveRecordId, Integer live) {
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (null == liveRecord) {
            throw new BsException(ErrorCode.LIVE_RECORD_NOT_EXIST);
        }
        liveRecord.setLive(live);
        if(LiveEnum.LIVE_CLOSE.getCode() == live) {
            liveRecord.setStatus(StatusEnum.INVALID.getCode());
        }
        liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
    }

    @Override
    public void systemRecycleNotLiveRecord(Long liveRecordId) {
        // TODO
    }

    @Override
    public void updateLiveRecord(LiveRecord liveRecord) {
        if(liveRecord == null) {
            return;
        }
        int res =  liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
        if (res < 1) {
            log.error("updateLiveRecord failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public int invalidLiveRecord(Long liveRecordId) {
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (null == liveRecord) {
            throw new BsException(ErrorCode.LIVE_RECORD_NOT_EXIST);
        }
        liveRecord.setLive(LiveEnum.LIVE_CLOSE.getCode());
        liveRecord.setStatus(StatusEnum.INVALID.getCode());
        return liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
    }

    @Override
    public List<LiveRecordDto> getLivingRecords(Integer liveType, String excludeUserUuid) {
        List<LiveRecord> liveRecords = liveRecordMapper.getLivingRecords(liveType, LiveEnum.LIVE.getCode(), excludeUserUuid);
        return modelMapper.map(liveRecords, new TypeToken<List<LiveDto>>() {
        }.getType());
    }

    @Override
    public LiveRecordDto getLiveRecordByRoomArchiveId(String roomArchiveId) {
        return Optional.ofNullable(liveRecordWrapper.selectByRoomArchiveId(roomArchiveId)).map(o -> modelMapper.map(o, LiveRecordDto.class)).orElse(null);
    }

    /**
     * 获取指定房间座位上的所有用户
     *
     * @param roomArchiveId 房间归档ID
     * @return 座位索引到用户信息的映射
     */
    @Override
    public List<UserOnSeatNotifyDto.SeatUser> getAllSeatUsersTyped(String roomArchiveId, String roomManager) {
        // 构建Redis key
        String neRoomMemberTableKey = NE_ROOM_SEAT_USER_TABLE_KEY.getKeyPrefix() + roomArchiveId;

        // 获取hash表中的所有键值对
        Map<Object, Object> rawMap = nemoRedisTemplate.opsForHash().entries(neRoomMemberTableKey);

        if (CollectionUtils.isEmpty(rawMap)) {
            return Lists.newArrayList();
        }

        return rawMap.values().stream()
                .map(o -> (UserOnSeatNotifyDto.SeatUser) o)
                .sorted((u1, u2) -> {
                    // 如果第一个用户是主播，则排在最前面
                    if (u1.getUserUuid().equals(roomManager)) {
                        return -1;
                    }
                    // 如果第二个用户是主播，则排在最前面
                    if (u2.getUserUuid().equals(roomManager)) {
                        return 1;
                    }
                    // 其他情况按时间戳排序
                    return Long.compare(u1.getIndex(), u2.getIndex());
                })
                .collect(Collectors.toList());
    }

    @Override
    public LiveRecordDto getLiveRecord(String userUuid, List<Integer> ongoingState) {
        return Optional.ofNullable(liveRecordWrapper.selectByUserUuidAndState(userUuid, ongoingState)).map(o -> modelMapper.map(o, LiveRecordDto.class)).orElse(null);
    }
}
