package com.netease.nemo.entlive.enums;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

@Getter
@AllArgsConstructor
public enum MusicPlayerActionEnum {

    PLAY(1, "歌曲播放或者继续播放"),
    PAUSE(2, "歌曲暂停"),
    ;

    private final int action;
    private final String desc;

    public static boolean checkAction(Integer action) {
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


    public static EventTypeEnum getEventType(Integer action, Boolean firstPlay) {
        MusicPlayerActionEnum musicAction = fromAction(action);
        if (musicAction == null) {
            throw new BsException(ErrorCode.BAD_REQUEST, "action参数错误");
        }
        if (PLAY == musicAction) {
            if (BooleanUtils.isTrue(firstPlay)) {
                return EventTypeEnum.ENT_MUSIC_PLAY;
            } else {
                return EventTypeEnum.ENT_MUSIC_RESUME_PLAY;
            }
        } else {
            return EventTypeEnum.ENT_MUSIC_PAUSE;
        }
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
