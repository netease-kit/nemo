package com.netease.nemo.openApi.dto.sud.enums;

import lombok.Getter;

/**
 * @Author：CH
 * @Date：2023/8/23 9:58 AM
 */
public enum PushEventEnum {
    USER_IN("user_in"),
    USER_OUT("user_out"),
    USER_READY("user_ready"),
    GAME_START("game_start"),
    CAPTAIN_CHANGE("captain_change"),
    USER_KICK("user_kick"),
    GAME_END("game_end"),
    GAME_SETTING("game_setting"),
    AI_ADD("ai_add"),
    ROOM_INFO("room_info"),
    QUICK_START("quick_start"),
    ROOM_CLEAR("room_clear"),
    GAME_CREATE("game_create"),
    GAME_DELETE("game_delete"),
    ;
    @Getter
    private final String event;

    PushEventEnum(String event) {
        this.event = event;
    }


}
