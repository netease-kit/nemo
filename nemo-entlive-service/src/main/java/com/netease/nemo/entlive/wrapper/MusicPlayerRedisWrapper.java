package com.netease.nemo.entlive.wrapper;

import com.google.common.collect.Lists;
import com.netease.nemo.entlive.dto.PlayDetailInfoDto;
import com.netease.nemo.entlive.enums.MusicPlayerStatusEnum;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.util.RedisUtil;
import com.netease.nemo.util.gson.GsonUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MusicPlayerRedisWrapper {


    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;


    public void musicReady(Long liveRecordId, Long orderId, String userUuid) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_READY_INFO_KEY.getKeyPrefix(), liveRecordId, orderId);
        nemoRedisTemplate.opsForHash().put(cacheKey, userUuid, MusicPlayerStatusEnum.READIED.getStatus());
        nemoRedisTemplate.expire(cacheKey, 60, TimeUnit.SECONDS);
    }

    public void deleteMusicReady(Long liveRecordId, Long orderId) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_READY_INFO_KEY.getKeyPrefix(), liveRecordId, orderId);
        nemoRedisTemplate.delete(cacheKey);
    }

    public List<String> getMusicReadyUsers(Long liveRecordId, Long orderId) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_READY_INFO_KEY.getKeyPrefix(), liveRecordId, orderId);
        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(cacheKey);
        if (entries.isEmpty()) {
            return null;
        }
        List<String> userUuids = Lists.newArrayList();
        for (Object value : entries.keySet()) {
            if (value != null) {
                userUuids.add((String) value);
            }
        }
        return userUuids;
    }

    public boolean isMusicReady(Long liveRecordId, Long orderId, String userUuid) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_READY_INFO_KEY.getKeyPrefix(), liveRecordId, orderId);
        Object status = nemoRedisTemplate.opsForHash().get(cacheKey, userUuid);
        return status != null && ((Integer) status) == MusicPlayerStatusEnum.READIED.getStatus();
    }


    public void putMusicPlayerInfo(Long liveRecordId, PlayDetailInfoDto playDetailInfoDto) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_INFO_KEY.getKeyPrefix(), liveRecordId);
        nemoRedisTemplate.opsForValue().set(cacheKey, GsonUtil.toJson(playDetailInfoDto),60, TimeUnit.MINUTES);
    }

    public PlayDetailInfoDto getMusicPlayerInfo(Long liveRecordId) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_INFO_KEY.getKeyPrefix(), liveRecordId);
        Object value = nemoRedisTemplate.opsForValue().get(cacheKey);
        if(value == null) {
            return null;
        }

        return GsonUtil.fromJson((String) value, PlayDetailInfoDto.class);
    }

    public void deleteMusicPlayerInfo(Long liveRecordId) {
        String cacheKey = RedisUtil.joinKey(RedisKeyEnum.ENT_MUSIC_PLAY_INFO_KEY.getKeyPrefix(), liveRecordId);
        nemoRedisTemplate.delete(cacheKey);
    }
}
