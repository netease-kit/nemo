package com.netease.nemo.socialchat.dto.rtc;

import lombok.Data;

@Data
public class RtcRoomUserInfoDto {
    private Long channelId;
    private String channelName;
    private Long createTime;
    private Long timestamp;
    private String user;
    private Long uid;
    private Long duration;
    private String userUuid;
    private String userName;
    private String icon;
    private Integer status;
}
