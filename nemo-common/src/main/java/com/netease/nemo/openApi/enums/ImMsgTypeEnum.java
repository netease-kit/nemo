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
    TEXT_MESSAGE(0,"TEXT"),
    PIC_MESSAGE(1,"PICTURE"),
    AUDIO_MESSAGE(2,"AUDIO"),
    VIDEO_MESSAGE(3,"VIDEO"),
    LOCATION_MESSAGE(4,"LOCATION"),
    FILE_MESSAGE(6,"TIPS"),
    NOTICE_MESSAGE(10,"NOTIFICATION"),
    CUSTOM_MESSAGE(100,"CUSTOM"),
    ;

    private final int type;
    private final String msgType;

    ImMsgTypeEnum(int type, String msgType) {
        this.type = type;
        this.msgType = msgType;
    }
}
