package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MusicPlayerStatusEnum {

    PLAY(1, "歌曲播放或者继续播放"),
    PAUSE(2, "歌曲暂停"),
    READYING(3, "歌曲准备中"),
    READIED(4, "歌曲准备完成"),
    ;

    private final int status;
    private final String desc;

    public static boolean checkStatus(Integer status) {
        if(null == status) {
            return false;
        }
        for (MusicPlayerStatusEnum value : MusicPlayerStatusEnum.values()) {
            if (value.status == status) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkMusicPlayerAction(Integer status) {
        if (null == status
                || (PLAY.getStatus() != status && PAUSE.getStatus() != status)) {
            return false;
        }
        return true;
    }

    /**
     * 根据状态编码获取枚举
     *
     * @param status 状态编码
     * @return 邀请操作枚举
     */
    public static MusicPlayerStatusEnum fromStatus(Integer status) {
        for (MusicPlayerStatusEnum value : MusicPlayerStatusEnum.values()) {
            if (value.status == status) {
                return value;
            }
        }
        return null;
    }
}
