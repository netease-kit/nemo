package com.netease.nemo.openApi.enums;

public enum ImEventEnum {

    /**
     * 抄送消息类型，eventType的值
     */
    CHATROOM_INOUT(9, "聊天室成员进出聊天室事件抄送"),
            ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    ImEventEnum(int i, String desc) {
        this.code = i;
        this.desc = desc;
    }

    public static ImEventEnum fromCode(int code) {
        for (ImEventEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
