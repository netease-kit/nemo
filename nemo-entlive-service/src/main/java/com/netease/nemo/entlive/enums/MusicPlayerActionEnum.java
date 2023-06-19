package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MusicPlayerActionEnum {

    PLAY(1, "歌曲播放或者继续播放"),
    PAUSE(2, "歌曲暂停"),
    ;

    private final int action;
    private final String desc;

    public static boolean checkSAction(Integer action) {
        if(null == action) {
            return false;
        }
        for (MusicPlayerActionEnum value : MusicPlayerActionEnum.values()) {
            if (value.action == action) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkMusicPlayerAction(Integer action) {
        if (null == action
                || (PLAY.getAction() != action && PAUSE.getAction() != action)) {
            return false;
        }
        return true;
    }

    /**
     * 根据状态编码获取枚举
     *
     * @param action 状态编码
     * @return 邀请操作枚举
     */
    public static MusicPlayerActionEnum fromAction(Integer action) {
        for (MusicPlayerActionEnum value : MusicPlayerActionEnum.values()) {
            if (value.action == action) {
                return value;
            }
        }
        return null;
    }
}
