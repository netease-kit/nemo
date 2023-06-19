package com.netease.nemo.entlive.parameter;

import lombok.Data;

@Data
public class SwitchSongParam {

    /**
     * 直播记录
     */
    private Long liveRecordId;

    /**
     * 被切当前点歌
     */
    private Long  currentOrderId;

    /**
     * 透传信息
     */
    private String attachment;


}
