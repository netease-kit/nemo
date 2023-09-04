package com.netease.nemo.socialchat.service.scheduler;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.enums.RtcStatusEnum;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.util.RedisUtil;
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

import static com.netease.nemo.enums.RedisKeyEnum.RTC_CLOUD_PLAYER_RTC_TASK_TABLE_KEY;
import static com.netease.nemo.enums.RedisKeyEnum.RTC_RECORD_KEY;

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

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    /**
     * 注：此处仅为1v1演示demo定时回收房间逻辑，落地时需根据业务自行实现
     * 每隔2分钟扫描RTC房间列表，如果rtc房间存活时间超过60分钟则回收
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void cleanRtcRoom() {
        Context context = Context.init(UUIDUtil.getUUID());
        long now = System.currentTimeMillis();
        MDC.put("traceId", context.getTraceId());
        cleanRtcRoom(yunXinConfigProperties.getAppKey(), now);
        MDC.clear();
    }


    public void cleanRtcRoom(String appKey, long now) {
        String tabKey = RedisUtil.springCacheJoinKey(RTC_RECORD_KEY.getKeyPrefix(), appKey);
        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(tabKey);
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        for (Object o : entries.values()) {
            if (o == null) {
                continue;
            }
            RtcRoomInfoDto rtcRoomInfoDto = (RtcRoomInfoDto) o;
            String taskTableKey = RedisUtil.springCacheJoinKey(RTC_CLOUD_PLAYER_RTC_TASK_TABLE_KEY.getKeyPrefix(), appKey, rtcRoomInfoDto.getChannelId());
            Map<Object, Object> rtcCloudPlayerTasks = nemoRedisTemplate.opsForHash().entries(taskTableKey);
            if (!CollectionUtils.isEmpty(rtcCloudPlayerTasks)) {
                log.info("房间存在云端播放任务:{}，房间不关闭", GsonUtil.toJson(rtcCloudPlayerTasks));
                continue;
            }
            if (RtcStatusEnum.START.getStatus() == rtcRoomInfoDto.getStatus() && rtcRoomInfoDto.getCreatetime() + _1v1RtcRoomLiveTime * 60 * 1000 < now) {
                log.info("rtc房间超过60分钟，开始关闭rtc房间：appKey:{}, ChannelId:{},ChannelName:{}", appKey, rtcRoomInfoDto.getChannelId(), rtcRoomInfoDto.getChannelName());
                try {
                    nimService.deleteRtcRoom(rtcRoomInfoDto.getChannelId());
                    nemoRedisTemplate.opsForHash().delete(tabKey, rtcRoomInfoDto.getChannelId());
                    log.info("关闭rtc房间成功：appKey:{}, ChannelId:{}, ChannelName:{}", appKey, rtcRoomInfoDto.getChannelId(), rtcRoomInfoDto.getChannelName());
                } catch (Exception e) {
                    log.info("deleteRtcRoom failed.", e);
                }
            }
        }
    }

    /**
     * 注：此逻辑仅为1v1娱乐社交维护在线用户列表逻辑，落地时需根据业务自行实现
     * 1v1娱乐社交定时清除上报时间超过5s的用户
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void cleanOfflineUser() {
        Context context = Context.init(UUIDUtil.getUUID());
        MDC.put("traceId", context.getTraceId());
        cleanOfflineUser(yunXinConfigProperties.getAppKey());
        MDC.clear();
    }

    private void cleanOfflineUser(String appKey) {
        String orderKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix(), appKey);
        long now = System.currentTimeMillis();
        log.info("start cleanOfflineUser,appKey:{}, orderKey:{}", appKey, orderKey);

        Set<Object> offlineUsers = nemoRedisTemplate.opsForZSet().rangeByScore(orderKey, Double.MIN_VALUE, now - 10000);
        if (!CollectionUtils.isEmpty(offlineUsers)) {
            log.info("offline Users,Users:{}", GsonUtil.toJson(offlineUsers));
            nemoRedisTemplate.opsForZSet().removeRangeByScore(orderKey, Double.MIN_VALUE, now - 10000);
        }
        log.info("end cleanOfflineUser,appKey:{}, orderKey:{}", appKey, orderKey);
    }

    /**
     * 注： 此定时器为云信1V1娱乐社交demo处理安全通审核的违规用户的逻辑：当检测到违规用户后，向客户端发送用户解禁消息IM系统通知，并从违规列表中移除。
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void unblockedUser() {
        Context context = Context.init(UUIDUtil.getUUID());
        MDC.put("traceId", context.getTraceId());
        unblockedUser(yunXinConfigProperties.getAppKey());
        MDC.clear();
    }

    private void unblockedUser(String appKey) {
        log.info("start unblockedUser,appKey:{}", appKey);
        String antispamViolationsUserTable = RedisUtil.springCacheJoinKey(RedisKeyEnum.ANTISPAM_VIOLATIONS_USER_TABLE_KEY.getKeyPrefix(), appKey);

        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(antispamViolationsUserTable);
        if (!CollectionUtils.isEmpty(entries)) {
            log.info("antispamViolationsUserList {}.", GsonUtil.toJson(entries));
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String k = (String) entry.getKey();
                Object v = entry.getValue();
                RtcAntispamDto rtcAntispamDto = (RtcAntispamDto) v;
                if (rtcAntispamDto != null) {
                    Long channelId = rtcAntispamDto.getChannelId();
                    RtcRoomInfoDto rtcRoomInfoDto = oneToOneChatService.getRtcRoomInfoDtoByChannelId(appKey, channelId);
                    if (null == rtcRoomInfoDto) {
                        log.info("rtcRoomInfoDto is null,channelId:{}", channelId);
                        continue;
                    }
                    List<RtcRoomUserInfoDto> roomUsers = oneToOneChatService.getRtcRoomUsersByChannelId(appKey, channelId);
                    try {
                        //TODO
                        // 实际业务处理由审核人员判断该违规用户是否能解禁
                        roomUsers.forEach(o -> socialChatMessageService.notifyUserUnblockedMessage(appKey, o));
                        nemoRedisTemplate.opsForHash().delete(antispamViolationsUserTable, k);
                    } catch (Exception e) {
                        log.info("send User Unblocked Message failed.", e);
                    }
                }
            }
        }
        log.info("end unblockedUser,appKey:{}", appKey);
    }
}
