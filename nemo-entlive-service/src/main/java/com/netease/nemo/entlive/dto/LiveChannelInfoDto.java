package com.netease.nemo.entlive.dto;

import lombok.Data;

/**
 * 直播频道信息
 */
@Data
public class LiveChannelInfoDto {
    private String taskId;
    /**
     * 频道ID
     */
    private String cid;

    private String pushUrl;

    private String pullRtsUrl;

    /**
     * HTTP 拉流地址
     */
    private String pullHttpUrl;

    /**
     * RTMP 拉流地址
     */
    private String pullRtmpUrl;

    /**
     * HLS 拉流地址
     */
    private String pullHlsUrl;
}
