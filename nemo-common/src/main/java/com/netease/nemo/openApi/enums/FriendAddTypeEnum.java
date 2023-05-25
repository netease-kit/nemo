package com.netease.nemo.openApi.enums;

import lombok.Getter;

@Getter
public enum FriendAddTypeEnum {
    ADD_FRIEND(1, "直接加好友"),
    ADD_FRIEND_APPLY(2, "请求加好友"),
    ADD_FRIEND_AGREE(3, "同意加好友"),
    ADD_FRIEND_REJECT(4, "好友拒绝"),
    ;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    FriendAddTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 检测code是否合法
     *
     * @param code code
     * @return 是否合法
     */
    public static Boolean checkCode(Integer code) {
        for (FriendAddTypeEnum e : FriendAddTypeEnum.values()) {
            if (code == e.code) {
                return true;
            }
        }
        return false;
    }
}
