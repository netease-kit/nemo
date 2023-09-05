package com.netease.nemo.entlive.delay.listener;

import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.ListenTogetherPlayTask;
import com.netease.nemo.entlive.dto.PlayDetailInfoDto;
import com.netease.nemo.entlive.enums.MusicPlayerStatusEnum;
import com.netease.nemo.entlive.parameter.MusicActionParam;
import com.netease.nemo.entlive.service.MusicPlayService;
import com.netease.nemo.entlive.service.impl.MusicPlayServiceImpl;
import com.netease.nemo.entlive.wrapper.MusicPlayerRedisWrapper;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * LISTEN_TOGETHER_TOPIC队列消息处理
 *
 * @Author：CH
 * @Date：2023/8/8 9:45 PM
 */
@Component
@Slf4j
public class ListenTogetherPlayerListener implements InitializingBean {
    @Resource
    private DelayQueueConsumerRegister delayQueueConsumerRegister;
    @Resource
    private MusicPlayService musicPlayService;

    @Resource
    private MusicPlayerRedisWrapper musicPlayerRedisWrapper;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @RedisDelayListener(topic = DelayTopic.LISTEN_TOGETHER_TOPIC)
    public void ListenTogetherPlayer(DelayMessage delayMessage) {
        String traceId = UUIDUtil.getUUID();
        MDC.put("traceId", traceId);
        Context context = Context.init(traceId);

        try {
            if (null == delayMessage || StringUtils.isEmpty(delayMessage.getMessage())) {
                log.info("ListenTogetherPlayerListener: delayMessage is null，Abandon the delayMessage");
                return;
            }
            log.info("the topic:{},message:{} : ", delayMessage.getTopic(), delayMessage.getMessage());
            ListenTogetherPlayTask task = GsonUtil.fromJson(delayMessage.getMessage(), ListenTogetherPlayTask.class);
            if (task == null || task.getLiveRecordId() == null) {
                log.info("ListenTogetherPlayerListener: task or tLiveRecordId is null，Abandon the task");
                return;
            }
            Long liveRecordId = task.getLiveRecordId();
            Long orderId = task.getOrderId();
            List<String> userUuids = musicPlayerRedisWrapper.getMusicReadyUsers(liveRecordId, orderId);
            if (CollectionUtils.isEmpty(userUuids)) {
                log.info("ListenTogetherPlayerListener: userUuids is null，Abandon the task");
                return;
            }

            PlayDetailInfoDto playDetailInfoDto = musicPlayerRedisWrapper.getMusicPlayerInfo(liveRecordId);
            if (playDetailInfoDto == null || !playDetailInfoDto.getOrderId().equals(orderId)) {
                log.info("ListenTogetherPlayerListener: playDetailInfoDto is null or orderId is not equal，Abandon the task");
                return;
            }
            if (MusicPlayerStatusEnum.PLAY.getStatus() == playDetailInfoDto.getMusicStatus()) {
                log.info("ListenTogetherPlayerListener: music is playing, Abandon the task");
                return;
            }

            lockerService.tryLockAndDo(
                    () -> musicPlayService.musicAction(userUuids.get(0), new MusicActionParam(liveRecordId, MusicPlayerStatusEnum.PLAY.getStatus(), Boolean.TRUE)),
                    RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), liveRecordId);
        } finally {
            context.unload();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delayQueueConsumerRegister.registerTopic(DelayTopic.LISTEN_TOGETHER_TOPIC);
    }
}
