package com.netease.nemo.socialchat.service;

import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomUserNotifyParam;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;

public interface SecurityAuditService {

    void submitSecurityAuditTask(RtcRoomNotifyParam rtcRoomNotifyParam);

    void stopSecurityAuditTask(Long channelId, String channelName);

    void antispamHandler(RtcAntispamDto rtcAntispamDto);

}
