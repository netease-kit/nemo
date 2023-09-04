package com.netease.nemo.queue.aspect;

import com.netease.nemo.queue.DelayMessage;
import com.netease.nemo.util.gson.GsonUtil;
import com.netease.nemo.queue.annotation.RedisDelayListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 延迟队列消费注解切面
 *
 * @Author：CH
 * @Date：2023/8/8 4:58 PM
 */
@Component
@Slf4j
@Aspect
public class RedisDelayListenerAspect {

    @Around("@annotation(listener)")
    public Object around(ProceedingJoinPoint joinPoint, RedisDelayListener listener) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object obj : args) {
            if (obj instanceof DelayMessage && listener.topic().equals(((DelayMessage) obj).getTopic())) {
                log.info("消费者监听到消息：{}", GsonUtil.toJson(obj));
                return joinPoint.proceed();
            }
        }

        return null;
    }
}
