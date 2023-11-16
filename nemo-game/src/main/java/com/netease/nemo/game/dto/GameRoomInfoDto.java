package com.netease.nemo.game.dto;

import com.netease.nemo.game.model.po.GameRecord;
import lombok.Data;

/**
 * 游戏信息对象
 *
 * @Author：CH
 * @Date：2023/8/23 7:26 PM
 */
@Data
public class GameRoomInfoDto {
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 游戏描述
     */
    private String gameDesc;
    /**
     * 游戏图标
     */
    private String thumbnail;
    /**
     * 游戏规则
     */
    private String rule;

    /**
     * 房间编号
     */
    private String roomUuid;

    /**
     * 直播记录编号
     */
    private Long liveRecordId;

    private String  gameCreator;

    /**
     * 云信appKey
     */
    private String appKey;

    /**
     * NeRoom唯一主键
     */
    private String roomArchiveId;

    /**
     * 游戏状态
     */
    private Integer gameStatus;

    public static GameRoomInfoDto from(GameRecord gameRecord) {
        GameRoomInfoDto gameRoomInfoDto = new GameRoomInfoDto();
        gameRoomInfoDto.setGameId(gameRecord.getGameId());
        gameRoomInfoDto.setGameName(gameRecord.getGameName());
        gameRoomInfoDto.setGameDesc(gameRecord.getGameDesc());
        gameRoomInfoDto.setThumbnail(gameRecord.getThumbnail());
        gameRoomInfoDto.setRule(gameRecord.getRule());
        gameRoomInfoDto.setRoomUuid(gameRecord.getRoomUuid());
        gameRoomInfoDto.setLiveRecordId(gameRecord.getLiveRecordId());
        gameRoomInfoDto.setRoomArchiveId(gameRecord.getRoomArchiveId());
        gameRoomInfoDto.setGameStatus(gameRecord.getGameStatus());
        gameRoomInfoDto.setGameCreator(gameRecord.getGameCreator());
        return gameRoomInfoDto;
    }
}
