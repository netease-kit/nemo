package com.netease.nemo.entlive.enums;

public enum SingModeEnum {
    SMART_CHORUS(0, "智能合唱"),
    SERIAL_CHORUS(1, "串行合唱"),
    LIVE_CHORUS(2, "NTP实时合唱"),
    SOLO(3, "独唱"),
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    SingModeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
