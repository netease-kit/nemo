package com.netease.nemo.socialchat.parameter.rtcNotify;

import lombok.Data;

@Data
public class RtcRoomUserNotifyParam {
    private Long channelId;
    private String channelName;
    private Long createTime;
    private Long timestamp;
    private String user;
    private Long uid;
    private Long duration;
    private Integer platform;
    private Integer userRole;
    private Integer status;
    private String appKey;
}
