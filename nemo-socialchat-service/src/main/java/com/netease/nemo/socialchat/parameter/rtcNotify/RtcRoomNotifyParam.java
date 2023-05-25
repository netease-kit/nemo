package com.netease.nemo.socialchat.parameter.rtcNotify;

import lombok.Data;

@Data
public class RtcRoomNotifyParam {
    private Long channelId;
    private String channelName;
    private Long createtime;
    private Long timestamp;
    private Integer status;
}
