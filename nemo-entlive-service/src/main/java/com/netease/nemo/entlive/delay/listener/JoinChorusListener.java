package com.netease.nemo.entlive.delay.listener;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.JoinChorusTask;
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
 * 副唱加入合唱后延迟任务处理，当副唱加入合唱后，延迟一段时间后，如果副唱歌曲资源依旧没有ready，则直接
 *
 * @Author：CH
 * @Date：2023/8/8 9:45 PM
 */
@Component
@Slf4j
public class JoinChorusListener implements InitializingBean {
    @Resource
    private DelayQueueConsumerRegister delayQueueConsumerRegister;

    @Resource
    private SingService singService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @RedisDelayListener(topic = DelayTopic.JOIN_CHORUS_TOPIC)
    public void joinChorus(DelayMessage delayMessage) {
        String traceId = UUIDUtil.getUUID();
        MDC.put("traceId", traceId);
        Context context = Context.init(traceId);

        try {
            if (null == delayMessage || StringUtils.isEmpty(delayMessage.getMessage())) {
                log.info("joinChorusListen: delayMessage is null，Abandon the delayMessage");
                return;
            }
            log.info("the topic:{},message:{} : ", delayMessage.getTopic(), delayMessage.getMessage());
            JoinChorusTask task = GsonUtil.fromJson(delayMessage.getMessage(), JoinChorusTask.class);
            if (task == null || task.getChorusId() == null || task.getRoomUuid() == null || task.getOrderId() == null) {
                log.info("ListenTogetherPlayerListener: task or ChorusId/RoomUuid/OrderId is null，Abandon the task");
                return;
            }
            Long orderId = task.getOrderId();
            String roomUuid = task.getRoomUuid();
            String chorusId = task.getChorusId();

            lockerService.tryLockAndDo(
                    () -> singService.joinChorusTimeOut(roomUuid, chorusId, orderId),
                    RedisKeyEnum.ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), yunXinConfigProperties.getAppKey() , roomUuid);
        } finally {
            context.unload();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delayQueueConsumerRegister.registerTopic(DelayTopic.JOIN_CHORUS_TOPIC);
    }
}
