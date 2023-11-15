package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KtvActionEnum {
    PAUSE(0, "歌曲暂停"),
    PLAY(1, "歌曲继续播放"),
    END(2, "歌曲结束"),
    ;

    private final int action;
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param action action
     * @return 邀请操作枚举
     */
    public static KtvActionEnum fromAction(Integer action) {
        for (KtvActionEnum value : KtvActionEnum.values()) {
            if (value.action == action) {
                return value;
            }
        }
        return null;
    }
}
