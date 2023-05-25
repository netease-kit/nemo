package com.netease.nemo.socialchat.dto.message;

import lombok.Data;

@Data
public class UserUnBlockMessage {
    /**
     * 音视频房间 ID
     **/
    private Long channelId;

    /**
     * 音视频房间名称
     **/
    private String channelName;

    /**
     * 音视频uid
     */
    private Long uid;

    public UserUnBlockMessage(Long channelId, String channelName, Long uid) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.uid = uid;
    }
}
