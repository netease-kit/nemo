package com.netease.nemo.queue.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 延迟队列生产者
 *
 * @name：caohao
 * @Date：2023/8/8 4:17 PM
 */
@Component
@Slf4j
public class DelayQueueProducer {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    /**
     * 发送延迟消息
     *
     * @param topic   队列topic
     * @param message message
     * @param delay   延迟时间
     */
    public void send(String topic, String message, long delay) {
        log.info("发送延迟消息：topic={},message={},delay={}", topic, message, delay);
        nemoRedisTemplate.opsForZSet().add(topic, message, System.currentTimeMillis() + delay);
    }

    /**
     * 取消延迟消息
     *
     * @param topic   队列topic
     * @param message message
     */
    public void cancel(String topic, String message) {
        log.info("取消延迟消息：topic={},message={}", topic, message);
        nemoRedisTemplate.opsForZSet().remove(topic, message);
    }
}
