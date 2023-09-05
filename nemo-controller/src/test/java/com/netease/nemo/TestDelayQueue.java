package com.netease.nemo;

import com.netease.nemo.queue.producer.DelayQueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author：CH
 * @Date：2023/8/8 10:15 PM
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDelayQueue {

    @Resource
    private DelayQueueProducer delayQueueProducer;

    @Test
    public void test1() {
        delayQueueProducer.send("listen_together_topic", "hello", 1000);
    }

}
