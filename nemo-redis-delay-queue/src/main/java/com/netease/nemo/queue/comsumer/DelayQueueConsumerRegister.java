package com.netease.nemo.queue.comsumer;

import com.netease.nemo.util.gson.GsonUtil;
import com.netease.nemo.queue.DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列消费者注册
 *
 * @Author：CH
 * @Date：2023/8/8 4:16 PM
 */
@Component
@Slf4j
public class DelayQueueConsumerRegister implements ApplicationContextAware, DisposableBean {

    private Set<String> topicSet = new CopyOnWriteArraySet<>();

    private ApplicationContext applicationContext;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    private volatile State state = State.NEW;

    /**
     * 默认取10个任务
     */
    private final int batchSize = 10;

    public void registerTopic(String topic) {
        if (state == State.TERMINATED) {
            return;
        }
        log.info("registerTopic:{} ", topic);
        state = State.RUNNING;
        if (!topicSet.contains(topic)) {
            new Thread(new Consumer(topic)).start();
            topicSet.add(topic);
        }
    }


    class Consumer implements Runnable {

        private String topic;

        public Consumer(String topic) {
            this.topic = topic;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("delay-queue-consumer-thread-" + "-" + topic);
            String key = topic;
            while (state == State.RUNNING) {
                try {
                    long now = Instant.now().toEpochMilli();
                    Set<Object> readyJobs = null;
                    try {
                        Set<Object> jobs = nemoRedisTemplate.opsForZSet().rangeByScore(key, 0, now, 0, batchSize);

                        if (!CollectionUtils.isEmpty(jobs)) {
                            nemoRedisTemplate.opsForZSet().remove(key, jobs.toArray());
                        }

                        readyJobs = jobs;
                    } catch (Exception e) {
                        log.error("redis error, topic:{}", topic, e);
                    }
                    if (CollectionUtils.isEmpty(readyJobs)) {
                        sleep();
                        continue;
                    }

                    for (Object o : readyJobs) {
                        String message = (String) o;
                        DelayMessage delayMessage = new DelayMessage(this, topic, message);
                        log.info("receive delayed job:{}", GsonUtil.toJson(delayMessage));
                        applicationContext.publishEvent(delayMessage);
                    }
                } catch (Throwable e) {
                    log.error("unexpected consume error, topic:{}", topic, e);
                    sleep();
                }
            }
            log.info("consumer terminated, topic:{}", topic);
        }

        private void sleep() {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    enum State {
        NEW, RUNNING, TERMINATED
    }

    @Override
    public void destroy() throws Exception {
        state = State.TERMINATED;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
