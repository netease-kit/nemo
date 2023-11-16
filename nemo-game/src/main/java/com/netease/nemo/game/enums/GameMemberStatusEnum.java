package com.netease.nemo.game.enums;

import lombok.Getter;

/**
 * @Author：CH
 * @Date：2023/8/30 2:47 PM
 */
@Getter
public enum GameMemberStatusEnum {
    READYING(0, "准备中，待开始"),
    GAMING(1, "游戏中"),
    GAME_LEAVE(2, "离开游戏"),
    ;

    /**
     * 状态编码
     */
    private final int status;
    /**
     * 描述
     */
    private final String desc;

    GameMemberStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
