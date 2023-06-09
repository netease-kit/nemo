package com.netease.nemo.entlive.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrderSongDto {

    /**
     * 直播唯一记录编号
     */
    private Long liveRecordId;

    @SerializedName("orderId")
    private Long id;

    /**
     * NeRoom虚拟房间唯一编号
     */
    private String roomArchiveId;

    /**
     * 点歌用户userUuid
     */
    private String userUuid;

    /**
     * 关联roomUuid
     */
    private String roomUuid;

    /**
     * 歌曲
     */
    private String songId;

    /**
     * 歌曲
     */
    private String songName;

    /**
     * 歌曲封面
     */
    private String songCover;

    /**
     * 歌手名称
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

    /**
     * CANCEL(-1, "删除点歌"),
     * WAITING(0, "等待唱歌或者等待播放"),
     * PLAYING(1, "唱歌中或者播放中"),
     * PLAYED(2, "已唱或者已播放"),;
     */
    private Integer status;
}

