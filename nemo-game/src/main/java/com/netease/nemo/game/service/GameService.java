package com.netease.nemo.game.service;

import com.netease.nemo.game.dto.GameInfoDto;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import com.netease.nemo.game.dto.GameRoomMemberDto;
import com.netease.nemo.game.request.GameInfoParam;
import com.netease.nemo.game.request.GameRoomParam;

import java.util.List;

/**
 * 游戏相关业务接口
 *
 * @Author：CH
 * @Date：2023/8/23 7:25 PM
 */
public interface GameService {


    /**
     * 获取当前应用下游戏列表
     *
     * @return List<GameInfoDto>
     */
    List<GameInfoDto> getGameList();

    /**
     * 创建游戏房间
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     * @return GameRoomInfoDto
     */
    GameRoomInfoDto createGame(String userUuid, GameRoomParam gameRoomParam);

    /**
     * 用户加入游戏
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     * @return GameRoomMemberDto
     */
    GameRoomMemberDto joinGame(String userUuid, GameRoomParam gameRoomParam);

    /**
     * 用户退出游戏
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     */
    void exitGame(String userUuid, GameRoomParam gameRoomParam);

    /**
     * 获取游戏房间信息
     *
     * @param gameInfoParam 游戏房间信息
     * @return GameInfoDto
     */
    GameRoomInfoDto getGameInfo(GameInfoParam gameInfoParam);

    /**
     * 获取游戏房间信息
     *
     * @param roomUuid 房间uuId
     * @return GameInfoDto
     */
    GameRoomInfoDto getGameInfo(String roomUuid);

    /**
     * 开始游戏
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     */
    void startGame(String userUuid, GameRoomParam gameRoomParam);

    /**
     * 结束游戏
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     * @param isSendMsg     是否发送Neroom消息
     */
    void endGame(String userUuid, GameRoomParam gameRoomParam, Boolean isSendMsg);

    /**
     * 获取游戏房间成员列表
     *
     * @param userUuid      用户uuid
     * @param gameRoomParam 游戏房间参数
     * @return List<GameRoomMemberDto>
     */
    List<GameRoomMemberDto> getGameRoomMembers(String userUuid, GameRoomParam gameRoomParam);

    /**
     * 用户游戏状态上报
     *
     * @param userUuid      userUuid
     * @param gameRoomParam gameRoomParam
     */
    void statusReporter(String userUuid, GameRoomParam gameRoomParam);
}
