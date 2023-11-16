package com.netease.nemo.game.listener;

import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.entlive.event.GameRoomCloseEvent;
import com.netease.nemo.entlive.event.GameUserLeaveRoomEvent;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import com.netease.nemo.game.enums.GameStatusEnum;
import com.netease.nemo.game.request.GameRoomParam;
import com.netease.nemo.game.service.GameService;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 游戏事件监听处理
 *
 * @Author：CH
 * @Date：2023/9/07 9:45 PM
 */
@Component
@Slf4j
public class GameEventListener {

    @Resource
    private GameService gameService;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @EventListener(classes = {GameUserLeaveRoomEvent.class})
    public void gameUserLeave(GameUserLeaveRoomEvent gameUserLeaveRoomEvent) {
        String appKey = yunXinConfigProperties.getAppKey();
        log.info("start gameUserLeaveEvent: gameUserLeaveRoomEvent: {}", GsonUtil.toJson(gameUserLeaveRoomEvent));
        if (gameUserLeaveRoomEvent == null || StringUtils.isAnyBlank(gameUserLeaveRoomEvent.getRoomUuid(), gameUserLeaveRoomEvent.getUserUuid(), appKey)) {
            log.info("GameUserLeaveListener: task or RoomUuid|UserUuid|AppKey is null，Abandon the task");
            return;
        }
        String roomUuid = gameUserLeaveRoomEvent.getRoomUuid();
        String userUuid = gameUserLeaveRoomEvent.getUserUuid();

        GameRoomInfoDto gameRoomInfoDto = gameService.getGameInfo(roomUuid);
        if (gameRoomInfoDto == null || gameRoomInfoDto.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            log.info("GameUserLeaveListener: gameRoomInfoDto is null or The GameRecord is Ended，Abandon the task");
            return;
        }
        lockerService.tryLockAndDo(() -> gameService.exitGame(userUuid, new GameRoomParam(gameRoomInfoDto.getGameId(), roomUuid)), RedisKeyEnum.GAME_ROOM_LOCK_KEY.getKeyPrefix(), roomUuid);
    }

    @EventListener(classes = {GameRoomCloseEvent.class})
    public void gameRoomClose(GameRoomCloseEvent gameRoomCloseEvent) {
        String appKey = yunXinConfigProperties.getAppKey();
        log.info("start gameRoomClose: gameRoomCloseEvent: {}", GsonUtil.toJson(gameRoomCloseEvent));
        if (gameRoomCloseEvent == null || StringUtils.isAnyBlank(gameRoomCloseEvent.getRoomUuid())) {
            log.info("GameRoomCloseTask: task or RoomUuid is null，Abandon the task");
            return;
        }
        String roomUuid = gameRoomCloseEvent.getRoomUuid();

        GameRoomInfoDto gameRoomInfoDto = gameService.getGameInfo(roomUuid);
        if (gameRoomInfoDto == null || gameRoomInfoDto.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            log.info("GameUserLeaveListener: gameRoomInfoDto is null or The GameRecord is Ended，Abandon the task");
            return;
        }
        lockerService.tryLockAndDo(
                () -> gameService.endGame(gameRoomInfoDto.getGameCreator(), new GameRoomParam(gameRoomInfoDto.getGameId(), roomUuid), false),
                RedisKeyEnum.GAME_ROOM_LOCK_KEY.getKeyPrefix(), roomUuid);
    }
}
