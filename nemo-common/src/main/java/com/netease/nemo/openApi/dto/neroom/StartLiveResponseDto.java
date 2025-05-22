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
     * 几个主播在会议音视频房间内的成员编号
     */
    private List<Long> liveAVRoomUids;


}
