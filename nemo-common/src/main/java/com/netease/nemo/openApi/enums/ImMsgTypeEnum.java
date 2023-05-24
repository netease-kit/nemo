package com.netease.nemo.openApi.enums;

import lombok.Getter;

/**
 * 0：文本消息
 * 1：图片消息
 * 2：语音消息
 * 3：视频消息
 * 4：地理位置消息
 * 6：文件消息
 * 10：提示消息
 * 100：自定义消息
 */
@Getter
public enum ImMsgTypeEnum {
    TEXT_MESSAGE(0),
    PIC_MESSAGE(1),
    AUDIO_MESSAGE(2),
    VIDEO_MESSAGE(3),
    LOCATION_MESSAGE(4),
    FILE_MESSAGE(6),
    NOTICE_MESSAGE(10),
    CUSTOM_MESSAGE(100),;
    private final int type;

    ImMsgTypeEnum(int type) {
        this.type = type;
    }
}
