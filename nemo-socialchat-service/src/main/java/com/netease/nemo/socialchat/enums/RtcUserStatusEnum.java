package com.netease.nemo.socialchat.enums;

import lombok.Getter;

@Getter
public enum RtcUserStatusEnum {
    IN_ROOM(1), LEAVE(2);
    private final int status;

    RtcUserStatusEnum(int status) {
        this.status = status;
    }


}
