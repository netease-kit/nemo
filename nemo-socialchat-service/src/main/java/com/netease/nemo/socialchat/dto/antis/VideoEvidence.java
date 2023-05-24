package com.netease.nemo.socialchat.dto.antis;

/**
 * 视频证据
 */
public class VideoEvidence {

    /**
     * 证据类型，1：图片，2：视频
     * **/
    private Integer type;

    /**
     * 证据信息
     * **/
    private String url;

    /**
     * 违规对应的用户ID
     * **/
    private Long uid;

    public VideoEvidence(Integer type, String url, Long uid) {
        this.type = type;
        this.url = url;
        this.uid = uid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
