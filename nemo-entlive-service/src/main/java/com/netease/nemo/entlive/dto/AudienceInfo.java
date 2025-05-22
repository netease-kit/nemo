package com.netease.nemo.entlive.dto;

import lombok.Data;

/**
 * 观众信息
 */
@Data
public class AudienceInfo {
    /**
     * 用户账号
     */
    private String userUuid;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String icon;

    /**
     * 进入时间
     */
    private Long enterTime;
}