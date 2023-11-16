package com.netease.nemo.game.wrapper;

import com.netease.nemo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.netease.nemo.enums.RedisKeyEnum.USER_GAME_STATUS_KEY;

/**
 * @Author：CH
 * @Date：2023/8/31 10:48 AM
 */
@Component
@Slf4j
public class UserGameStatusWrapper {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    public void setGameStatus(String roomUuid, String userUuid, Integer status) {
        String key = RedisUtil.springCacheJoinKey(USER_GAME_STATUS_KEY, roomUuid, userUuid);
        nemoRedisTemplate.opsForValue().set(key, status);
        nemoRedisTemplate.expire(key, 60, TimeUnit.SECONDS);
    }
}
