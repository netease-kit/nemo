package com.netease.nemo.entlive.parameter;

/**
 * 歌曲readyParam
 */
public class MusicReadyParam {

    /**
     * 直播间编号
     */
    private Long liveRecordId;

    /**
     * 点歌编号
     */
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getLiveRecordId() {
        return liveRecordId;
    }

    public void setLiveRecordId(Long liveRecordId) {
        this.liveRecordId = liveRecordId;
    }
}
