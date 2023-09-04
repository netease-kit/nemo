package com.netease.nemo.queue;

import org.springframework.context.ApplicationEvent;

/**
 * 延迟消息对象
 *
 * @name：caohao
 * @Date：2023/8/8 4:36 PM
 */
public class DelayMessage extends ApplicationEvent {
    /**
     * 队列 topic
     */
    private String topic;

    /**
     * 消息体
     */
    private String message;

    public DelayMessage(Object source, String topic, String message) {
        super(source);
        this.topic = topic;
        this.message = message;
    }

    public DelayMessage(Object source) {
        super(source);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
