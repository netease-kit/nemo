package com.netease.nemo.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class NemoConfig {

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedisTemplate<String, Object> nemoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean(name = "nemoRedissonClient")
    public RedissonClient nemoRedissonClient() {
        Config config = new Config();

        // 集群
        if (redisProperties.getCluster() != null) {
            config.useClusterServers().addNodeAddress(redisProperties.getCluster().getNodes().toArray(new String[0]));
            if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
                config.useClusterServers().setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }
        // 哨兵
        if (redisProperties.getSentinel() != null) {
            List<String> nodes = redisProperties.getSentinel().getNodes();
            List<String> newNodes = new ArrayList<>();
            nodes.forEach((index) -> newNodes.add(
                    index.startsWith("redis://") ? index : "redis://" + index));

            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(newNodes.toArray(new String[0]))
                    .setMasterName(redisProperties.getSentinel().getMaster());

            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }

        // 单机
        String url = String.format("redis://%s:%d", redisProperties.getHost(), redisProperties.getPort());
        config.useSingleServer().setAddress(url).setDatabase(redisProperties.getDatabase());
        if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
