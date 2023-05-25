package com.netease.nemo.socialchat.dto;

import lombok.Data;

@Data
public class OnLineUserDto {
    private String mobile;
    private String userUuid;
    private String userName;
    private String icon;
    private long firstReportTime;
    private long lastReportTime;
}
