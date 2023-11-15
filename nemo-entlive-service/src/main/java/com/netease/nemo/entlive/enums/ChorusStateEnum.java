package com.netease.nemo.entlive.enums;

public enum ChorusStateEnum {
    INVITING(0, "邀请中"),
    CHORUS_ING(1, "合唱中"),
    CHORUS_REJECT(2, "拒绝合唱"),
    CHORUS_CANCEL(3, "取消合唱"),
    CHORUS_END(4, "合唱结束"),
    CHORUS_JOIN(5, "加入合唱"),
    CHORUS_READY(6, "同意准备完成"),
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    ChorusStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    /**
     * 检测code是否合法
     *
     * @param code
     * @return
     */
    public static Boolean checkCode(Integer code) {
        if (null == code) {
            return false;
        }

        for (ChorusStateEnum e : ChorusStateEnum.values()) {
            if (code == e.code) {
                return true;
            }
        }
        return false;
    }
}
