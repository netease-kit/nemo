package com.netease.nemo.openApi.dto.nim;

/**
 * 云信创建直播频道结果
 */
public class YunxinCreateLiveChannelDto {

    /**
     * HTTP 拉流地址
     */
    private String httpPullUrl;

    /**
     * RTMP 拉流地址
     */
    private String rtmpPullUrl;

    /**
     * HLS 拉流地址
     */
    private String hlsPullUrl;

    /**
     * RTS 拉流地址
     */
    private String rtsPullUrl;

    /**
     * 推流地址
     */
    private String pushUrl;

    /**
     * 频道ID，32位字符串
     */
    private String cid;


    public String getHttpPullUrl() {
        return httpPullUrl;
    }

    public void setHttpPullUrl(String httpPullUrl) {
        this.httpPullUrl = httpPullUrl;
    }

    public String getRtmpPullUrl() {
        return rtmpPullUrl;
    }

    public void setRtmpPullUrl(String rtmpPullUrl) {
        this.rtmpPullUrl = rtmpPullUrl;
    }

    public String getHlsPullUrl() {
        return hlsPullUrl;
    }

    public void setHlsPullUrl(String hlsPullUrl) {
        this.hlsPullUrl = hlsPullUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getRtsPullUrl() {
        return rtsPullUrl;
    }

    public void setRtsPullUrl(String rtsPullUrl) {
        this.rtsPullUrl = rtsPullUrl;
    }
}
