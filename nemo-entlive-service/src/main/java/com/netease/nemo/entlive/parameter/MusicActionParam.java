package com.netease.nemo.entlive.parameter;

/**
 * 歌曲播放操作
 */
public class MusicActionParam {

    /**
     * 直播间编号
     */
    private Long liveRecordId;

    /**
     * 一起听歌曲操作 1：播放  2：暂停
     */
    private Integer action;

    /**
     * 是否第一次播放
     */
    private Boolean firstPlay;

    public MusicActionParam() {

    }

    public MusicActionParam(Integer action, Boolean firstPlay) {
       this.action = action;
       this.firstPlay = firstPlay;
   }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Boolean getFirstPlay() {
        return firstPlay;
    }

    public void setFirstPlay(Boolean firstPlay) {
        this.firstPlay = firstPlay;
    }

    public Long getLiveRecordId() {
        return liveRecordId;
    }

    public void setLiveRecordId(Long liveRecordId) {
        this.liveRecordId = liveRecordId;
    }
}
