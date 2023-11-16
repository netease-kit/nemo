package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum NeRoomNotifyEnum {
    CREATE_ROOM("CREATE_ROOM", "NeRoom房间创建成功"),
    CLOSE_ROOM("CLOSE_ROOM", "NeRoom房间关闭"),
    USER_JOIN_ROOM("USER_JOIN_ROOM", "加入NeRoom房间"),
    USER_LEAVE_ROOM("USER_LEAVE_ROOM", "离开NeRoom房间"),
    USER_ON_SEAT("USER_ON_SEAT", "用户上麦"),
    USER_OFF_SEAT("USER_OFF_SEAT", "用户下麦"),
    ;

    /**
     * 抄送事件类型
     */
    private final String eventType;
    /**
     * 描述
     */
    private final String desc;

    NeRoomNotifyEnum(String eventType, String desc) {
        this.eventType = eventType;
        this.desc = desc;
    }

    public static NeRoomNotifyEnum fromCode(String eventType) {
        for (NeRoomNotifyEnum value : values()) {
            if (value.eventType.equals(eventType)) {
                return value;
            }
        }
        return null;
    }
}
