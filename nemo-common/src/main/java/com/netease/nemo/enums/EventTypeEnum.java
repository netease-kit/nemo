package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum EventTypeEnum {
    MESSAGE_CONTENT_MODERATION(400, "实时音视频安全通审核事件"),
    CLOUD_RECORDING_CONTENT_MODERATION(401, "云端录制安全通审核事件"),


    SOCIAL_CHAT_USER_REWARD(1004, "1v1娱乐娱乐社交打赏事件"),
    SOCIAL_CHAT_USER_REWARD_YUN_XIN_ASSIST(1007, "1V1娱乐娱乐社交云信小秘书发送打赏消息事件"),

    USER_UNBLOCK(3000, "实时音视频安全通审核违规账号解禁"),
    ;


    /**
     * 状态编码
     */
    private final int type;
    /**
     * 描述
     */
    private final String desc;

    EventTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
