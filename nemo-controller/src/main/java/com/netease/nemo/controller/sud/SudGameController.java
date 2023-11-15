package com.netease.nemo.controller.sud;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.context.Context;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.game.dto.GameInfoDto;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import com.netease.nemo.game.dto.GameRoomMemberDto;
import com.netease.nemo.game.request.GameInfoParam;
import com.netease.nemo.game.request.GameRoomParam;
import com.netease.nemo.game.service.GameService;
import com.netease.nemo.locker.LockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 云信派对游戏接口
 *
 * @Author：CH
 * @Date：2023/8/23 5:47 PM
 */
@RequestMapping("/nemo/game/")
@Slf4j
@RestResponseBody
@RestController
public class SudGameController {

    @Resource
    private GameService gameService;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    /**
     * 查询游戏列表
     *
     * @return List<GameInfoDto>
     */
    @GetMapping("/list")
    @TokenAuth
    public List<GameInfoDto> getGameList() {
        return gameService.getGameList();
    }

    /**
     * 查询游戏列表
     *
     * @return List<GameInfoDto>
     */
    @PostMapping("/create")
    @TokenAuth
    public GameRoomInfoDto createGame(@Valid @RequestBody GameRoomParam gameRoomParam) {
        return lockerService.tryLockAndDoAndReturn(
                () -> gameService.createGame(Context.get().getUserUuid(), gameRoomParam),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY, gameRoomParam.getRoomUuid());
    }


    /**
     * 加入游戏房间
     */
    @PostMapping("/join")
    @TokenAuth
    public GameRoomMemberDto joinGame(@Valid @RequestBody GameRoomParam gameRoomParam) {
        return lockerService.tryLockAndDoAndReturn(
                () -> gameService.joinGame(Context.get().getUserUuid(), gameRoomParam),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY, gameRoomParam.getRoomUuid());
    }

    /**
     * 退出游戏房间
     */
    @PostMapping("/exit")
    @TokenAuth
    public void exitGame(@Valid @RequestBody GameRoomParam gameRoomParam) {
        lockerService.tryLockAndDo(
                () -> gameService.exitGame(Context.get().getUserUuid(), gameRoomParam),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY, gameRoomParam.getRoomUuid());
    }

    /**
     * 开始游戏
     */
    @PostMapping("/start")
    @TokenAuth
    public void startGame(@Valid @RequestBody GameRoomParam gameRoomParam) {
        lockerService.tryLockAndDo(
                () -> gameService.startGame(Context.get().getUserUuid(), gameRoomParam),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY, gameRoomParam.getRoomUuid());
    }

    /**
     * 开始游戏
     */
    @PostMapping("/end")
    @TokenAuth
    public void endGame(@Valid @RequestBody GameRoomParam gameRoomParam) {
        lockerService.tryLockAndDo(
                () -> gameService.endGame(Context.get().getUserUuid(), gameRoomParam, true),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY, gameRoomParam.getRoomUuid());
    }

    /**
     * 查询游戏房间成员查询
     *
     * @return List<GameInfoDto>
     */
    @GetMapping("/members")
    @TokenAuth
    public List<GameRoomMemberDto> getGameMembers(@Valid GameRoomParam gameRoomParam) {
        return gameService.getGameRoomMembers(Context.get().getUserUuid(), gameRoomParam);
    }

    /**
     * 查询游戏房间信息
     *
     * @return List<GameRoomInfoDto>
     */
    @GetMapping("/gameInfo")
    @TokenAuth
    public GameRoomInfoDto getGameInfo(@Valid GameInfoParam gameInfoParam) {
        return gameService.getGameInfo(gameInfoParam);
    }

    /**
     * 游戏状态reporter上报
     */
    @GetMapping("/status-reporter")
    @TokenAuth
    public void statusReporter(@Valid GameRoomParam gameRoomParam) {
        gameService.statusReporter(Context.get().getUserUuid(), gameRoomParam);
    }

}
