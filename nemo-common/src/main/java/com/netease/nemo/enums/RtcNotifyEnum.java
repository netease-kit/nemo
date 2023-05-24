package com.netease.nemo.enums;

public enum RtcNotifyEnum {
    EVENT_TYPE_ROOM_START(1, "房间开始"),
    EVENT_TYPE_ROOM_END(2, "房间结束"),
    EVENT_TYPE_ROOM_RECORD(3, "房间录制文件下载信息抄送"),
    EVENT_TYPE_ROOM_USER_IN(4, "房间成员加入"),
    EVENT_TYPE_ROOM_USER_OUT(5, "房间成员离开"),
    EVENT_TYPE_ROOM_DURATION(8, "房间时长抄送"),
    ROLE_CHANGE_TO_AUDIENCE(9, "用户角色变更为观众"),
    ROLE_CHANGE_TO_HOST(10, "用户角色变更为主播"),
    EVENT_TYPE_WB_RECORD_FILE(905, "白板录制抄送"),
    EVENT_TYPE_ROOM_SECURITY_AUDIT(400, "房间安全通审核抄送"),
    EVENT_TYPE_RECORD_ANTIS(401, "云端录制安全通审核抄送")
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    RtcNotifyEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RtcNotifyEnum fromCode(int code) {
        for (RtcNotifyEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
