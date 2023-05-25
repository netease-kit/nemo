package com.netease.nemo.socialchat.service.impl;

import com.google.gson.JsonObject;
import com.netease.nemo.enums.RtcNotifyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.service.NimNotifyService;
import com.netease.nemo.socialchat.enums.RtcStatusEnum;
import com.netease.nemo.socialchat.enums.RtcUserStatusEnum;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomUserNotifyParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SecurityAuditService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("socialChatNotifyServiceImpl")
@Slf4j
public class SocialChatNotifyServiceImpl implements NimNotifyService {

    @Resource
    private SecurityAuditService securityAuditService;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Override
    public void handlerNotify(String body) {
        handleRtcMsg(body);
    }

    /**
     * TODO 注：仅为云信派对1v1娱乐社交demo处理逻辑
     *  在演示项目中rtc相关的数据都保存在Redis中， 线上业务处理需结合实际业务需求实现
     */
    public void handleRtcMsg(String body) {
        log.info("handleRtcMsg body: {}", body);
        try {
            JsonObject jsonObject = GsonUtil.parseJsonObject(body);
            Integer eventType = GsonUtil.getInt(jsonObject, "eventType");
            RtcNotifyEnum eventTypeEnum = RtcNotifyEnum.fromCode(eventType);
            if (eventTypeEnum == null) {
                log.info("handleRtcMsg eventType:{} not support", eventType);
                return;
            }
            JsonObject data = GsonUtil.getJsonObject(jsonObject, "data");
            switch (eventTypeEnum) {
                case EVENT_TYPE_ROOM_START: {
                    RtcRoomNotifyParam param = GsonUtil.fromJson(data, RtcRoomNotifyParam.class);
                    param.setStatus(RtcStatusEnum.START.getStatus());
                    oneToOneChatService.saveRtcRecord(param);
                    securityAuditService.submitSecurityAuditTask(param);
                    break;
                }
                case EVENT_TYPE_ROOM_END: {
                    RtcRoomNotifyParam param = GsonUtil.fromJson(data, RtcRoomNotifyParam.class);
                    param.setStatus(RtcStatusEnum.END.getStatus());
                    securityAuditService.stopSecurityAuditTask(param.getChannelId(), param.getChannelName());
                    break;
                }
                case EVENT_TYPE_ROOM_USER_IN: {
                    RtcRoomUserNotifyParam param = GsonUtil.fromJson(data, RtcRoomUserNotifyParam.class);
                    param.setStatus(RtcUserStatusEnum.IN_ROOM.getStatus());
                    oneToOneChatService.saveRtcUserRecord(param);
                    break;
                }
                case EVENT_TYPE_ROOM_USER_OUT: {
                    RtcRoomUserNotifyParam param = GsonUtil.fromJson(data, RtcRoomUserNotifyParam.class);
                    param.setStatus(RtcUserStatusEnum.LEAVE.getStatus());
                    oneToOneChatService.saveRtcUserRecord(param);
                    break;
                }
                case EVENT_TYPE_ROOM_SECURITY_AUDIT:
                    RtcAntispamDto rtcAntispamDto = RtcAntispamDto.build(data);
                    securityAuditService.antispamHandler(rtcAntispamDto);
                    break;
                default:
                    break;
            }
        } catch (BsException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
