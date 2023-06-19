package com.netease.nemo.entlive.parameter;

import lombok.Data;

@Data
public class OrderSongParam {

    /**
     * 直播记录
     */
    private Long liveRecordId;

    /**
     * 歌曲
     */
    private String  songId;

    /**
     * 歌曲名称
     */
    private String songName;

    /**
     * 歌曲封面
     */
    private String songCover;

    /**
     * 歌手
     */
    private String singer;

    /**
     * 歌曲时长
     */
    private long songTime;

    /**
     * 渠道
     */
    private Integer channel;
}
