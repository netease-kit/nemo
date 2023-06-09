package com.netease.nemo.entlive.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    VALID(1, "有效"),
    INVALID(-1, "无效"),;

    /** 状态编码 */
    private final int code;
    /** 描述 */
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    /**
     * 检测code是否合法
     * @param code code
     * @return true or false
     */
    public static Boolean checkCode(Integer code) {
        for (StatusEnum e : StatusEnum.values()) {
            if (code == e.code) {
                return true;
            }
        }
        return false;
    }
}
