package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KtvSingStatusEnum {

    PAUSE(0, "演唱暂停"),
    PLAY(1, "演唱继续播放"),
    END(2, "演唱结束"),
    READY(3, "歌曲准备完成"),
    NOT_SING(4, "未开始演唱"),
    INVITING(5, "邀请中"),
    JOIN(6, "加入合唱"),
    ;
    private final int status;
    private final String desc;

    public static boolean checkStatus(Integer status) {
        if(null == status) {
            return false;
        }
        for (KtvSingStatusEnum value : KtvSingStatusEnum.values()) {
            if (value.status == status) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据状态编码获取枚举
     *
     * @param status 状态编码
     * @return 邀请操作枚举
     */
    public static KtvSingStatusEnum fromStatus(Integer status) {
        for (KtvSingStatusEnum value : KtvSingStatusEnum.values()) {
            if (value.status == status) {
                return value;
            }
        }
        return null;
    }

    public static boolean endStatus(Integer status) {
        if(null == status) {
            return false;
        }
        return END.getStatus() == status;
    }
}
