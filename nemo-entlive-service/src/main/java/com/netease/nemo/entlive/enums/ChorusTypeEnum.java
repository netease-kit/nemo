package com.netease.nemo.entlive.enums;

/**
 * 合唱类型枚举类
 * **/
public enum ChorusTypeEnum {
    SERIAL_CHORUS(1, "串行合唱"),
    REALTIME_CHORUS(2, "实时合唱"),
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    ChorusTypeEnum(int code, String desc) {
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
