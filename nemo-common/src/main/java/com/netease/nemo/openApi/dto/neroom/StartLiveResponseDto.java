package com.netease.nemo.openApi.dto.neroom;

import lombok.Data;

import java.util.List;


@Data
public class StartLiveResponseDto {


    /**
     * 直播任务编号
     */
    private String taskId;

    /**
     * 直播推流地址
     */
    private String pushUrl;

    /**
     * 直播聊天室房间号
     */
    private String chatRoomId;

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

    /**
     * TRS 拉流地址
     */
    private String pullRtsUrl;

    /**
     * 直播标题
     */
    private String title;


    /**
     * 直播布局
     */
    private Integer liveLayout;

    /**
     * 是否使用独立的直播聊天室，如果不使用独立的直播聊天室，那么使用会议的聊天室作为直播聊天室
     */
    private Boolean liveChatRoomIndependent;


    /**
     * 几个主播在会议音视频房间内的成员编号
     */
    private List<Long> liveAVRoomUids;


    /**
     * 端上需要的透传消息，端上返回传字符串
     */
    private String extensionConfig;

    /**
     * 是否是外部直播，外部直播不需要刷新防盗链
     */
    private Boolean externalLive;




}
