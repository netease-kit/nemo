package com.netease.nemo.controller.entertainmentLive;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.queue.producer.DelayQueueProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author：CH
 * @Date：2023/8/8 10:31 PM
 */
@RestController
@RequestMapping("/nemo/redis-queue/")
@Slf4j
@RestResponseBody
@TokenAuth
public class RedisQueueController {

    @Resource
    private DelayQueueProducer delayQueueProducer;


    @PostMapping(value = "/send")
    public void send(String topic, String message, Long delay) {
        delayQueueProducer.send(topic, message, delay);
    }

    @PostMapping(value = "/cancel")
    public void cancel(String topic, String message) {
        delayQueueProducer.cancel(topic, message);
    }
}
