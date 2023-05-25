package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum UserStateEnum {
    NORMAL(1), FORBIDDEN(2);
    private final int state;

    UserStateEnum(int state) {
        this.state = state;
    }
}
