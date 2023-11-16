package com.netease.nemo.entlive.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.entlive.enums.SeatStatusEnum;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.netease.nemo.enums.RedisKeyEnum.NE_ROOM_MEMBER_TABLE_KEY;

/**
 * NeRoom 房间成员服务
 *
 * @Author：CH
 * @Date：2023/5/18 10:57 下午
 */
@Service
@Slf4j
public class NeRoomMemberServiceImpl implements NeRoomMemberService {


    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private NeRoomService neRoomService;

    @Override
    public List<RoomMember> getRoomMembers(String roomArchiveId) {
        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + roomArchiveId;

        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(neRoomMemberTableKey);
        return CollectionUtils.isEmpty(entries) ? Lists.newArrayList() : entries.values().stream().map(e -> (RoomMember) e).collect(Collectors.toList());
    }

    @Override
    public long getRoomMemberSize(String roomArchiveId) {
        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + roomArchiveId;
        return nemoRedisTemplate.opsForHash().size(neRoomMemberTableKey);
    }

    @Override
    public void deleteRoomMembers(String roomArchiveId, List<RoomMember> roomMembers) {
        if(CollectionUtils.isEmpty(roomMembers)) {
            return;
        }
        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + roomArchiveId;
        roomMembers.forEach(o -> nemoRedisTemplate.opsForHash().delete(neRoomMemberTableKey, o.getUserUuid()));
    }

    @Override
    public boolean userInNeRoom(String roomArchiveId, String userUuid) {
        if (StringUtils.isAnyEmpty(roomArchiveId, userUuid)) {
            return false;
        }
        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + roomArchiveId;
        return null != nemoRedisTemplate.opsForHash().get(neRoomMemberTableKey, userUuid);
    }

    public boolean isUserOnSeat(String roomArchiveId, String userUuid) {
        List<NeRoomSeatDto> neRoomSeats = getSeatList(roomArchiveId);
        if (CollectionUtils.isEmpty(neRoomSeats)) {
            return false;
        }
        return neRoomSeats.stream()
                .filter(o -> o.getStatus() == SeatStatusEnum.ON_SEAT.getStatus())
                .anyMatch(o -> o.getUser().getUserUuid().equals(userUuid));
    }

    /**
     * 获取房间座位信息
     *
     * @param roomArchiveId roomArchiveId
     * @return List<NeRoomSeatDto>
     */
    public List<NeRoomSeatDto> getSeatList(String roomArchiveId) {
        List<NeRoomSeatDto> result;
        String cacheKey = org.apache.commons.lang3.StringUtils.join("GetSeatList", roomArchiveId);
        Object cacheResult = cache.getIfPresent(cacheKey);
        if (cacheResult != null) {
            result = (List<NeRoomSeatDto>) cacheResult;
            return result;
        }

        List<NeRoomSeatDto> neRoomSeatDto = neRoomService.getNeRoomSeatList(roomArchiveId);
        if (null != neRoomSeatDto) {
            cache.put(cacheKey, neRoomSeatDto);
        }
        return neRoomSeatDto;
    }

    private final Cache<String, Object> cache = Caffeine.newBuilder()
            .initialCapacity(200)//初始大小
            .maximumSize(500)//最大数量
            .expireAfterWrite(2, TimeUnit.SECONDS)//过期时间
            .build();
}
