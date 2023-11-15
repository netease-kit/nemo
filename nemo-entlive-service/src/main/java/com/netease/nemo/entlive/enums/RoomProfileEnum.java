package com.netease.nemo.entlive.enums;


public enum RoomProfileEnum {

    COMMUNICATION(0), //通话场景
    LIVE_BROADCASTING(1); // 直播场景

    private final int state;

    public int getState() {
        return state;
    }

    RoomProfileEnum(int state) {
        this.state = state;
    }


    public static boolean isBroadcasting(Integer state) {
        if(state == null) {
            return false;
        }
        return state == LIVE_BROADCASTING.getState();
    }

    public static RoomProfileEnum fromCode(Integer state) {
        for (RoomProfileEnum value : RoomProfileEnum.values()) {
            if (value.state == state) {
                return value;
            }
        }
       return null;
    }
}
