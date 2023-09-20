package com.netease.nemo.config;

import lombok.Data;

@Data
public class YunXinConfigProperties {
    /**
     * 云信appkey
     */
    private String appKey;

    /**
     * 云信appSecret
     */
    private String appSecret;

    /**
     * 云信IM服务地址
     */
    private String nimHost;

    /**
     * 云信直播频道服务地址
     */
    private String neLiveHost;

    /**
     * 云信NeRoom服务地址
     */
    private String neRoomHost;

    /**
     * 云信安全通服务地址
     */
    private String securityAuditHost;

    /**
     * 云信RTC服务地址
     */
    private String rtcHost;
}
