package com.netease.nemo.socialchat.service.scheduler;

import com.netease.nemo.context.Context;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class SocialChatScheduler {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Autowired
    private SocialChatMessageService socialChatMessageService;

    @Value("${business.1v1RtcRoomLiveTime:10}")
    private Integer _1v1RtcRoomLiveTime;

    @Resource
    private NimService nimService;


    /**
     * 注：此逻辑仅为1v1娱乐社交维护在线用户列表逻辑，落地时需根据业务自行实现
     * 1v1娱乐社交定时清除上报时间超过5s的用户
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void cleanOfflineUser() {
        String orderKey = RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix();

        Context context = Context.init(UUIDUtil.getUUID());
        long now = System.currentTimeMillis();
        MDC.put("traceId", context.getTraceId());
        Set<Object> offlineUsers = nemoRedisTemplate.opsForZSet().rangeByScore(orderKey, Double.MIN_VALUE, now - 5000);
        if (!CollectionUtils.isEmpty(offlineUsers)) {
            log.info("offline Users,Users:{}", GsonUtil.toJson(offlineUsers));
            nemoRedisTemplate.opsForZSet().removeRangeByScore(orderKey, Double.MIN_VALUE, now - 5000);
        }
        MDC.clear();
    }

    /**
     * 注： 此定时器为云信1V1娱乐社交demo处理安全通审核的违规用户的逻辑：当检测到违规用户后，向客户端发送用户解禁消息IM系统通知，并从违规列表中移除。
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void unblockedUser() {
        String antispamViolationsUserTable = RedisKeyEnum.ANTISPAM_VIOLATIONS_USER_LIST_KEY.getKeyPrefix();

        Context context = Context.init(UUIDUtil.getUUID());
        MDC.put("traceId", context.getTraceId());

        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(antispamViolationsUserTable);
        if (!CollectionUtils.isEmpty(entries)) {
            log.info("antispamViolationsUserList {}.", GsonUtil.toJson(entries));
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String k = (String) entry.getKey();
                Object v = entry.getValue();
                RtcAntispamDto rtcAntispamDto = (RtcAntispamDto) v;
                if (rtcAntispamDto != null) {
                    Long channelId = rtcAntispamDto.getChannelId();
                    RtcRoomInfoDto rtcRoomInfoDto = oneToOneChatService.getRtcRoomInfoDtoByChannelId(channelId);
                    if (null == rtcRoomInfoDto) {
                        log.info("rtcRoomInfoDto is null,channelId:{}", channelId);
                        continue;
                    }
                    List<RtcRoomUserInfoDto> roomUsers = oneToOneChatService.getRtcRoomUsersByChannelId(channelId);
                    try {
                        //TODO
                        // 实际业务处理由审核人员判断该违规用户是否能解禁
                        roomUsers.forEach(o -> socialChatMessageService.notifyUserUnblockedMessage(o));
                        nemoRedisTemplate.opsForHash().delete(antispamViolationsUserTable, k);
                    } catch (Exception e) {
                        log.info("send User Unblocked Message failed.", e);
                    }
                }
            }
        }
        MDC.clear();
    }
}
