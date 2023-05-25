package com.netease.nemo.openApi.enums;

import lombok.Getter;

/**
 * 0：单聊消息
 * 1：群消息（高级群）
 */
@Getter
public enum ImOpeEnum {
    SINGLE_CHAT_MESSAGE(0),
    GROUP_MESSAGE(1);
    private final int ope;

    ImOpeEnum(int ope) {
        this.ope = ope;
    }
}
