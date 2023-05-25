package com.netease.nemo.socialchat.enums;

import lombok.Getter;

@Getter
public enum RtcStatusEnum {
    START(1), END(2);
    private final int status;
    RtcStatusEnum(int status) {
        this.status = status;
    }
}
