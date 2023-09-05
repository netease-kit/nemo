package com.netease.nemo.entlive.enums;

import lombok.Getter;

@Getter
public enum LiveEnum {
    LIVE_CLOSE(-1, "直播结束"),
    NOT_START(0, "初始化"),
    LIVE(1, "直播中"),
    PK_LIVE(2, "PK中"),
    LIVE_PUNISHMENT(3, "直播惩罚"),
    LIVE_SEAT(4, "连麦中"),
    PK_WAITING(5, "pK 邀请中"),
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

    /**
     * 判断状态是否PK中
     *
     * @param code code
     * @return true or false
     */
    public static Boolean isPKLive(Integer code) {
        if (null == code) {
            return false;
        }

        return LiveEnum.PK_WAITING.getCode() == code
                || LiveEnum.LIVE_PUNISHMENT.getCode() == code
                || LiveEnum.PK_LIVE.getCode() == code;
    }

    /**
     * 判断状态是否可以PK
     *
     * @param code code
     * @return true or false
     */
    public static Boolean canPK(Integer code) {
        if (null == code) {
            return false;
        }

        return LiveEnum.LIVE.getCode() == code;
    }

}
