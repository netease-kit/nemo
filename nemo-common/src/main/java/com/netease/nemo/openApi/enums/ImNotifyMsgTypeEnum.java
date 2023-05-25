package com.netease.nemo.openApi.enums;

import lombok.Getter;

/**
 * 0：点对点自定义通知
 * 1：群消息自定义通知
 */
@Getter
public enum ImNotifyMsgTypeEnum {
    P2P(0),
    GROUP(1),
    ;
    private final int msgtype;

    ImNotifyMsgTypeEnum(int msgtype) {
        this.msgtype = msgtype;
    }
}
