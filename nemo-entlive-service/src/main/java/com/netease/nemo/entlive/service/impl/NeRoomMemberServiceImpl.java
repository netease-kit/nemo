package com.netease.nemo.entlive.service.impl;

import com.google.common.collect.Lists;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
}
