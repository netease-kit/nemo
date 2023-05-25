package com.netease.nemo.socialchat.dto.rtc;

import lombok.Data;

@Data
public class RtcRoomInfoDto {
    private Long channelId;
    private String channelName;
    private Long createtime;
    private Long timestamp;
    private Integer status;
}
