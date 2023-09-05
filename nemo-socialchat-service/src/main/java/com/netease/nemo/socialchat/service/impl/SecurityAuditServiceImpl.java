package com.netease.nemo.socialchat.service.impl;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.openApi.paramters.SubmitLiveWallSolutionTaskParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SecurityAuditService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.util.RedisUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SecurityAuditServiceImpl implements SecurityAuditService {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private NimService nimService;

    @Value(value = "business.systemAccid")
    private String systemAccid;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Resource
    private SocialChatMessageService socialChatMessageService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Override
    public void submitSecurityAuditTask(RtcRoomNotifyParam rtcRoomNotifyParam) {
        if (rtcRoomNotifyParam == null) {
            return;
        }
        // TODO 获取安全通uid, 此处应使用分布式唯一ID生成保证uid唯一性
        Long uid = System.nanoTime();
        SubmitLiveWallSolutionTaskParam liveWallSolutionTask = new SubmitLiveWallSolutionTaskParam(rtcRoomNotifyParam.getChannelName(), uid, 0, 5);
        nimService.submitSolution(liveWallSolutionTask);
    }

    @Override
    public void stopSecurityAuditTask(Long channelId, String channelName) {
        nimService.feedbackSolution(channelName);
    }

    /**
     * 安全通审核结果处理，仅为demo演示处理逻辑，实际场景实现需要根据客户的业务需求自行实现
     * 1、记录安全通数据
     * 2、给客户端发送用户违规消息
     *
     * @param rtcAntispamDto 安全通审核数据
     */
    @Override
    public void antispamHandler(RtcAntispamDto rtcAntispamDto) {
        String appKey = yunXinConfigProperties.getAppKey();
        log.info("start handler anti-spam data,rtcAntispamDto:{}", GsonUtil.toJson(rtcAntispamDto));
        if (rtcAntispamDto == null) {
            return;
        }

        RtcAntispamDto.Evidence evidence = rtcAntispamDto.getEvidences();
        if (evidence == null) {
            log.info("安全通机审证据信息为空");
            return;
        }
        RtcAntispamDto.Audio audio = evidence.getAudio();
        RtcAntispamDto.Video video = evidence.getVideo();
        if (audio == null && video == null) {
            log.info("音视频安全通机审证据信息为空");
            return;
        }

        // 解析违规用户uid
        Long uid = null;
        if (audio != null) {
            uid = audio.getUid();
        }
        if (video != null) {
            uid = video.getEvidence().getUid();
        }

        // 记录安全通审核结果数据
        String aViolationsListKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ANTISPAM_VIOLATIONS_LIST_KEY.getKeyPrefix(), appKey);
        nemoRedisTemplate.opsForList().leftPush(aViolationsListKey, rtcAntispamDto);
        nemoRedisTemplate.expire(aViolationsListKey, 30, TimeUnit.DAYS);

        // 记录违规的用户及对应的违规证据
        String aViolationsTabKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ANTISPAM_VIOLATIONS_USER_TABLE_KEY.getKeyPrefix(), appKey);
        nemoRedisTemplate.opsForHash().put(aViolationsTabKey, uid + "", rtcAntispamDto);
        nemoRedisTemplate.expire(aViolationsTabKey, 1, TimeUnit.DAYS);


        // 给客户端发送用户音视频违规的消息
        socialChatMessageService.notifyAntispamMessage(appKey, rtcAntispamDto);
    }
}
