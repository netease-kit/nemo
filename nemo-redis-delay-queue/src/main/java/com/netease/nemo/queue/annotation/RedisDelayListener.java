package com.netease.nemo.queue.annotation;

import org.springframework.context.event.EventListener;

import java.lang.annotation.*;

/**
 * 延迟队列消费者监听注解
 *
 * @name：caohao
 * @Date：2023/8/8 4:55 PM
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface RedisDelayListener {

    /**
     * 队列 topic
     *
     * @return
     */
    String topic();
}
