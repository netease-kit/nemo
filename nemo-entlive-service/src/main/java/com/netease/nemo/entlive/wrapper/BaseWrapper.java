package com.netease.nemo.entlive.wrapper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author：CH
 * @name：caohao
 * @Date：2023/6/2 8:06 下午
 */
public class BaseWrapper {

    @Resource(name = "nemoRedisTemplate")
    protected RedisTemplate<String, Object> nemoRedisTemplate;

    protected void deleteKey(org.springframework.data.redis.connection.RedisConnection connection, String key) {
        RedisSerializer<String> serializer = nemoRedisTemplate.getStringSerializer();
        byte[] keyBytes = Objects.requireNonNull(serializer.serialize(key));
        connection.del(keyBytes);
    }
}
