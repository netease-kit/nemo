package com.netease.nemo.socialchat.service.impl;

import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.openApi.paramters.SubmitLiveWallSolutionTaskParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SecurityAuditService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SecurityAuditServiceImpl implements SecurityAuditService {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private NimService nimService;

    @Value(value="business.systemAccid")
    private String systemAccid;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Resource
    private SocialChatMessageService socialChatMessageService;

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
        nemoRedisTemplate.opsForList().leftPush(RedisKeyEnum.ANTISPAM_VIOLATIONS_LIST_KEY.getKeyPrefix(), rtcAntispamDto);

        // 记录违规的用户及对应的违规证据
        nemoRedisTemplate.opsForHash().put(RedisKeyEnum.ANTISPAM_VIOLATIONS_USER_LIST_KEY.getKeyPrefix(), uid + "", rtcAntispamDto);

        // 给客户端发送用户音视频违规的消息
        socialChatMessageService.notifyAntispamMessage(rtcAntispamDto);
    }
}
