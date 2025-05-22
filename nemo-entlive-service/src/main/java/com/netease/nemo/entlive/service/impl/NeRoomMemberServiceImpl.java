package com.netease.nemo.entlive.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.entlive.dto.AudienceInfo;
import com.netease.nemo.entlive.enums.SeatStatusEnum;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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

    private static final String AUDIENCE_LIST_KEY_PREFIX = "chatroom:audience:list:";

    /**
     * 观众数量的Redis key前缀
     */
    private static final String AUDIENCE_COUNT_KEY_PREFIX = "chatroom:audience:count:";

    /**
     * 抄送消息处理锁前缀
     */
    private static final String CHATROOM_INOUT_LOCK_PREFIX = "lock:chatroom:inout:";

    /**
     * Redis中记录用户最后进入时间的key前缀
     */
    private static final String AUDIENCE_LAST_ENTER_TIME_PREFIX = "chatroom:audience:last_enter:";

    /**
     * Redis中记录用户最后离开时间的key前缀
     */
    private static final String AUDIENCE_LAST_LEAVE_TIME_PREFIX = "chatroom:audience:last_leave:";

    /**
     * 数据过期时间(24小时)
     */
    private static final int EXPIRE_TIME = 24 * 60 * 60;
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

    /**
     * 处理用户进入聊天室事件
     */
    @Override
    public void handleUserEnter(Long chatRoomId, String accid, long timestamp) {
        String lastEnterKey = AUDIENCE_LAST_ENTER_TIME_PREFIX + chatRoomId + ":" + accid;
        String lastLeaveKey = AUDIENCE_LAST_LEAVE_TIME_PREFIX + chatRoomId + ":" + accid;
        String audienceListKey = AUDIENCE_LIST_KEY_PREFIX + chatRoomId;
        String audienceCountKey = AUDIENCE_COUNT_KEY_PREFIX + chatRoomId;

        // 获取用户上次离开时间
        Object lastLeaveObj = nemoRedisTemplate.opsForValue().get(lastLeaveKey);
        Long lastLeave = lastLeaveObj != null ? Long.valueOf(lastLeaveObj.toString()) : null;

        // 如果没有离开记录或者当前进入时间晚于上次离开时间，则处理进入事件
        if (lastLeave == null || timestamp > lastLeave) {
            // 记录最新的进入时间
            nemoRedisTemplate.opsForValue().set(lastEnterKey, timestamp, EXPIRE_TIME, TimeUnit.SECONDS);

            // 检查用户是否已经在列表中
            Double score = nemoRedisTemplate.opsForZSet().score(audienceListKey, accid);
            if (score == null) {
                // 添加到在线列表，分数为进入时间, 如果有自定义排序需求，可以考虑将分数设置为自己需要排序的业务属性
                nemoRedisTemplate.opsForZSet().add(audienceListKey, accid, timestamp);
                // 增加计数器
                nemoRedisTemplate.opsForValue().increment(audienceCountKey);

                log.info("User entered chatroom: roomId={}, accid={}, timestamp={}", chatRoomId, accid, timestamp);
            } else {
                // 用户已在列表中，更新时间戳
                nemoRedisTemplate.opsForZSet().add(audienceListKey, accid, timestamp);
                log.info("Updated user enter time: roomId={}, accid={}, timestamp={}", chatRoomId, accid, timestamp);
            }

            // 设置过期时间
            nemoRedisTemplate.expire(audienceListKey, EXPIRE_TIME, TimeUnit.SECONDS);
            nemoRedisTemplate.expire(audienceCountKey, EXPIRE_TIME, TimeUnit.SECONDS);
        } else {
            log.info("Ignored outdated enter event: roomId={}, accid={}, timestamp={}, lastLeave={}",
                    chatRoomId, accid, timestamp, lastLeave);
        }
    }

    /**
     * 处理用户离开聊天室事件
     */
    @Override
    public void handleUserLeave(Long chatRoomId, String accid, long timestamp) {
        String lastEnterKey = AUDIENCE_LAST_ENTER_TIME_PREFIX + chatRoomId + ":" + accid;
        String lastLeaveKey = AUDIENCE_LAST_LEAVE_TIME_PREFIX + chatRoomId + ":" + accid;
        String audienceListKey = AUDIENCE_LIST_KEY_PREFIX + chatRoomId;
        String audienceCountKey = AUDIENCE_COUNT_KEY_PREFIX + chatRoomId;

        // 获取用户上次进入时间
        Object lastEnterObj = nemoRedisTemplate.opsForValue().get(lastEnterKey);
        Long lastEnter = lastEnterObj != null ? Long.valueOf(lastEnterObj.toString()) : null;

        // 如果有进入记录且当前离开时间晚于上次进入时间，则处理离开事件
        if (lastEnter != null && timestamp > lastEnter) {
            // 记录最新的离开时间
            nemoRedisTemplate.opsForValue().set(lastLeaveKey, timestamp, EXPIRE_TIME, TimeUnit.SECONDS);

            // 检查用户是否在列表中
            Double score = nemoRedisTemplate.opsForZSet().score(audienceListKey, accid);
            if (score != null) {
                // 从在线列表移除
                nemoRedisTemplate.opsForZSet().remove(audienceListKey, accid);
                // 减少计数器
                Long count = nemoRedisTemplate.opsForValue().decrement(audienceCountKey);
                // 确保计数不为负
                if (count != null && count < 0) {
                    nemoRedisTemplate.opsForValue().set(audienceCountKey, 0);
                }

                log.info("User left chatroom: roomId={}, accid={}, timestamp={}", chatRoomId, accid, timestamp);
            } else {
                log.info("User not in audience list when leaving: roomId={}, accid={}", chatRoomId, accid);
            }
        } else {
            log.info("Ignored outdated leave event: roomId={}, accid={}, timestamp={}, lastEnter={}",
                    chatRoomId, accid, timestamp, lastEnter);
        }
    }

    /**
     * 获取聊天室在线观众列表
     * @param chatRoomId 聊天室ID
     * @param page 页码(从0开始)
     * @param size 每页大小
     * @return 观众列表
     */
    @Override
    public List<AudienceInfo> getOnlineAudienceList(Long chatRoomId, int page, int size) {
        String audienceListKey = AUDIENCE_LIST_KEY_PREFIX + chatRoomId;

        // 按照时间排序获取观众列表(分页)
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = nemoRedisTemplate.opsForZSet().rangeWithScores(
                audienceListKey, (long) (page - 1) * size, (long) (page) * size - 1);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }

        List<AudienceInfo> result = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            AudienceInfo info = new AudienceInfo();
            info.setUserUuid(tuple.getValue().toString());
            info.setEnterTime(tuple.getScore().longValue());
            result.add(info);
        }

        return result;
    }

    /**
     * 获取聊天室在线观众数量
     * @param chatRoomId 聊天室ID
     * @return 观众数量
     */
    @Override
    public Long getOnlineAudienceCount(Long chatRoomId) {
        if(chatRoomId == null){
            return 0L;
        }
        String audienceCountKey = AUDIENCE_COUNT_KEY_PREFIX + chatRoomId;
        Object count = nemoRedisTemplate.opsForValue().get(audienceCountKey);
        return count != null ? Long.parseLong(count.toString()) : 0L;
    }
}
