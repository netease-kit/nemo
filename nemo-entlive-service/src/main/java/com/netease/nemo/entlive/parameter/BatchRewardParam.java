package com.netease.nemo.entlive.parameter;


import java.util.List;

public class BatchRewardParam {

    /**
     * 房间编号
     */
    private Long liveRecordId;

    /**
     * 直播正在PK时，不能为空
     */
    private String pkId;

    /**
     * 礼物编号
     */
    private Long giftId;

    /**
     * 礼物个数 默认1
     */
    private Integer giftCount;

    /**
     * 主播和连麦者的账号list
     */
    private List<String> userUuids;

    public Long getLiveRecordId() {
        return liveRecordId;
    }

    public void setLiveRecordId(Long liveRecordId) {
        this.liveRecordId = liveRecordId;
    }

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getPkId() {
        return pkId;
    }

    public void setPkId(String pkId) {
        this.pkId = pkId;
    }

    public Integer getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(Integer giftCount) {
        this.giftCount = giftCount;
    }

    public List<String> getUserUuids() {
        return userUuids;
    }

    public void setUserUuids(List<String> userUuids) {
        this.userUuids = userUuids;
    }
}
