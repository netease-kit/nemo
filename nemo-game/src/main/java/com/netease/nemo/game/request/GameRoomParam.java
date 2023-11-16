package com.netease.nemo.game.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：CH
 * @Date：2023/8/24 11:28 AM
 */
@Data
public class GameRoomParam {
    /**
     * 游戏id
     */
    @NotEmpty(message = "{gameId.notNull}")
    private String gameId;

    /**
     * liveRecord中关联的房间id
     */
    @NotEmpty(message = "{roomUuid.notNull}")
    private String roomUuid;

    public GameRoomParam() {
    }

    public GameRoomParam(String gameId, String roomUuid) {
        this.gameId = gameId;
        this.roomUuid = roomUuid;
    }
}
