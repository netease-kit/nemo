package com.netease.nemo.socialchat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnLineUserDto {
    private String mobile;
    private String userUuid;
    private String userName;
    private String icon;
    private String audioUrl;
    private String videoUrl;
    private int callType;
    private long firstReportTime;
    private long lastReportTime;
}
