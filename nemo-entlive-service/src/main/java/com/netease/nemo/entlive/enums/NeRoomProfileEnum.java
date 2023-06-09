package com.netease.nemo.entlive.enums;


import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.exception.BsException;
import lombok.Getter;

@Getter
public enum NeRoomProfileEnum {
    COMMUNICATION(0), //通话场景
    LIVE_BROADCASTING(1); // 直播场景

    private final int state;

    public final int getState() {
        return state;
    }

    NeRoomProfileEnum(int state) {
        this.state = state;
    }


    public static boolean isBroadcasting(Integer state) {
        if (state == null) {
            return false;
        }
        return state == LIVE_BROADCASTING.getState();
    }

    public static NeRoomProfileEnum fromCode(Integer state) {
        for (NeRoomProfileEnum value : NeRoomProfileEnum.values()) {
            if (value.state == state) {
                return value;
            }
        }
        throw new BsException(ErrorCode.BAD_REQUEST, "NeRoomProfileEnum error");
    }
}
