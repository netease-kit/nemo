package com.netease.nemo.game.enums;

import lombok.Getter;

/**
 * @Author：CH
 * @Date：2023/8/30 2:47 PM
 */
@Getter
public enum GameStatusEnum {
    READYING(0, "准备中，待开始"),
    GAMING(1, "游戏中"),
    GAME_END(-1, "游戏结束"),
    ;

    /**
     * 状态编码
     */
    private final int status;
    /**
     * 描述
     */
    private final String desc;

    GameStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
