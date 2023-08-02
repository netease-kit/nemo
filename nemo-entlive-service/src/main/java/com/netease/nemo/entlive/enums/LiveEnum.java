package com.netease.nemo.entlive.enums;

import lombok.Getter;

@Getter
public enum LiveEnum {
    LIVE_CLOSE(-1, "直播结束"),
    NOT_START(0, "初始化"),
    LIVE(1, "直播中"),
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    LiveEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 检测code是否合法
     *
     * @param code code
     * @return true or false
     */
    public static Boolean checkCode(Integer code) {
        if (null == code) {
            return false;
        }

        for (LiveEnum e : LiveEnum.values()) {
            if (code == e.code) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断状态是否在直播（直播包括 直播中 连麦中 PK中 惩罚中）
     *
     * @param code code
     * @return true or false
     */
    public static Boolean isLive(Integer code) {
        if (null == code) {
            return false;
        }

        return LiveEnum.LIVE_CLOSE.getCode() != code && LiveEnum.NOT_START.getCode() != code;
    }
}
