package com.netease.nemo.entlive.delay.listener;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.PlayNextSongTask;
import com.netease.nemo.entlive.service.SingService;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.queue.DelayMessage;
import com.netease.nemo.queue.annotation.RedisDelayListener;
import com.netease.nemo.queue.comsumer.DelayQueueConsumerRegister;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 播放下一首消息发出后，指定时间内(20s)未收到开始演唱或结束演唱或放弃演唱则视为放弃
 *
 * @Author：CH
 * @Date：2023/8/8 9:45 PM
 */
@Component
@Slf4j
public class AbandCurrentAndPlayNextSongListener implements InitializingBean {

    @Resource
    private DelayQueueConsumerRegister delayQueueConsumerRegister;

    @Resource
    private SingService singService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;


    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @RedisDelayListener(topic = DelayTopic.PLAY_NEXT_SONG_TOPIC)
    public void playNextSongTopic(DelayMessage delayMessage) {
        String traceId = UUIDUtil.getUUID();
        MDC.put("traceId", traceId);
        Context context = Context.init(traceId);

        try {
            if (null == delayMessage || StringUtils.isEmpty(delayMessage.getMessage())) {
                log.info("playNextSongTopicListener: delayMessage is null，Abandon the delayMessage");
                return;
            }
            log.info("the topic:{},message:{} : ", delayMessage.getTopic(), delayMessage.getMessage());
            PlayNextSongTask task = GsonUtil.fromJson(delayMessage.getMessage(), PlayNextSongTask.class);
            if (task == null || StringUtils.isEmpty(task.getRoomUuid()) || null == task.getOrderId() ) {
                log.info("playNextSongTopicListener: task or RoomUuid|OrderId is null，Abandon the task");
                return;
            }
            Long orderId = task.getOrderId();
            String roomUuid = task.getRoomUuid();

            lockerService.tryLockAndDo(
                    () -> singService.playNextSongTimeOut(roomUuid, orderId),
                    RedisKeyEnum.ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), yunXinConfigProperties.getAppKey(), roomUuid);
        } finally {
            context.unload();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delayQueueConsumerRegister.registerTopic(DelayTopic.PLAY_NEXT_SONG_TOPIC);
    }
}
