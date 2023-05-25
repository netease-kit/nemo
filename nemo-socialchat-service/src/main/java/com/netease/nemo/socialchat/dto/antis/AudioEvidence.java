package com.netease.nemo.socialchat.dto.antis;

/**
 * 语音证据
 */
public class AudioEvidence {

    /**
     * 违规内容
     * **/
    private String content;

    /**
     * 检测结果，1表示嫌疑，2表示不通过
     * **/
    private Integer action;

    /**
     * 违规对应的用户ID
     * **/
    private Long uid;

    public AudioEvidence(String content, Integer action, Long uid) {
        this.content = content;
        this.action = action;
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
