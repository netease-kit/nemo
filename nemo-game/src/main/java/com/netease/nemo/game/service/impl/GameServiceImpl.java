package com.netease.nemo.game.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.service.LiveRecordService;
import com.netease.nemo.entlive.service.MessageService;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.game.dto.GameInfoDto;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import com.netease.nemo.game.dto.GameRoomMemberDto;
import com.netease.nemo.game.enums.GameMemberStatusEnum;
import com.netease.nemo.game.enums.GameStatusEnum;
import com.netease.nemo.game.model.po.GameMember;
import com.netease.nemo.game.model.po.GameRecord;
import com.netease.nemo.game.request.GameInfoParam;
import com.netease.nemo.game.request.GameRoomParam;
import com.netease.nemo.game.service.GameService;
import com.netease.nemo.game.service.SudUserService;
import com.netease.nemo.game.util.GameResourceUtil;
import com.netease.nemo.game.wrapper.GameMemberMapperWrapper;
import com.netease.nemo.game.wrapper.GameRecordMapperWrapper;
import com.netease.nemo.game.wrapper.UserGameStatusWrapper;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.SudService;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import com.netease.nemo.openApi.dto.sud.MgInfo;
import com.netease.nemo.openApi.dto.sud.enums.PushEventEnum;
import com.netease.nemo.openApi.dto.sud.event.*;
import com.netease.nemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 云信游戏服务
 *
 * @Author：CH
 * @Date：2023/8/25 3:03 PM
 */
@Service
@Slf4j
public class GameServiceImpl implements GameService {

    @Resource
    private SudService sudService;

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private GameRecordMapperWrapper gameRecordMapperWrapper;

    @Resource
    private GameMemberMapperWrapper gameMemberMapperWrapper;

    @Resource
    private UserGameStatusWrapper userGameStatusWrapper;

    @Resource
    private UserService userService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private SudUserService sudUserService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Value("${business.game.onlineGames}")
    private String[] onlineGames;

    @Override
    public List<GameInfoDto> getGameList() {
        List<MgInfo> mgList = sudService.gameList();
        if (CollectionUtils.isEmpty(mgList)) {
            return Collections.emptyList();
        }

        return mgList.stream()
                .filter(o -> Arrays.stream(onlineGames).collect(Collectors.toList()).contains(o.getMgId()))
                .map(o -> {
                    GameInfoDto gameInfoDto = GameInfoDto.from(o, getLanguageCode());
                    gameInfoDto.setRule(GameResourceUtil.gameMap.get(o.getMgId()));
                    return gameInfoDto;
                }).collect(Collectors.toList());
    }

    @Override
    public GameRoomInfoDto createGame(String userUuid, GameRoomParam gameRoomParam) {
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(gameRoomParam.getRoomUuid());
        if (!userUuid.equals(liveRecordDto.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限创建游戏");
        }
        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        GameRecord gameRecordExist = gameRecordMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid());
        if (gameRecordExist != null) {
            if (!gameRoomParam.getGameId().equals(gameRecordExist.getGameId())) {
                throw new BsException(ErrorCode.BAD_REQUEST, "房间已开始其他游戏");
            }
            return GameRoomInfoDto.from(gameRecordExist);
        }

        MgInfo mgInfo = sudService.gameInfo(gameRoomParam.getGameId());
        GameInfoDto gameInfoDto = GameInfoDto.from(mgInfo, getLanguageCode());
        GameRecord gameRecord = GameRecord.buildGameRecord(liveRecordDto, gameInfoDto);
        if (!StringUtils.isEmpty(GameResourceUtil.gameMap.get(mgInfo.getMgId()))) {
            gameRecord.setRule(GameResourceUtil.gameMap.get(mgInfo.getMgId()));
        }
        gameRecordMapperWrapper.insertSelective(gameRecord);

        sendGameMessage(gameRecord, gameRoomParam.getRoomUuid(), EventTypeEnum.GAME_CREATE.getType());

        return GameRoomInfoDto.from(gameRecord);
    }

    @Override
    public GameRoomMemberDto joinGame(String userUuid, GameRoomParam gameRoomParam) {
        String roomUuid = gameRoomParam.getRoomUuid();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(gameRoomParam.getRoomUuid());

        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid());
        if (gameRecord == null || gameRecord.getGameStatus() != GameStatusEnum.READYING.getStatus()) {
            throw new BsException(ErrorCode.BAD_REQUEST, "游戏房间不存在|已结束|玩家正在游戏中");
        }

        if(!gameRoomParam.getGameId().equals(gameRecord.getGameId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "游戏gameId不匹配");
        }

        List<GameMember> gameMembers = gameMemberMapperWrapper.selectByGameRecordId(gameRecord.getId());
        GameMember gameExist = gameMembers.stream().filter(o -> o.getUserUuid().equals(userUuid)).findFirst().orElse(null);
        if (gameExist != null) {
            gameExist.setStatus(GameMemberStatusEnum.READYING.getStatus());
            gameExist.setJoinTime(System.currentTimeMillis());
            gameMemberMapperWrapper.updateByPrimaryKey(gameExist);
            return modelMapper.map(gameExist, GameRoomMemberDto.class);
        }

        List<NeRoomSeatDto> seats = neRoomMemberService.getSeatList(liveRecordDto.getRoomArchiveId());
        long gameUserCount = gameMembers.stream().filter(o -> o.getStatus() != GameMemberStatusEnum.GAME_LEAVE.getStatus()).count();
        if (CollectionUtils.isEmpty(seats)
                || gameUserCount >= seats.size()) {
            throw new BsException(ErrorCode.FORBIDDEN, "游戏坐席已满");
        }

        if (!neRoomMemberService.isUserOnSeat(liveRecordDto.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户不在麦上");
        }

        UserDto userDto = userService.getUser(userUuid);
        GameMember gameMember = GameMember.buildGameMember(liveRecordDto, gameRecord, userDto);
        gameMemberMapperWrapper.insertSelective(gameMember);

        UserInReqData userInReqData = UserInReqData.builder().code(sudUserService.getCode(yunXinConfigProperties.getAppKey(), userUuid)).roomId(roomUuid).mode(1).seatIndex(-1).isReady(true).build();
        sudService.pushGameEvent(PushEventEnum.USER_IN.getEvent(), gameRecord.getGameId(), String.valueOf(System.currentTimeMillis()), userInReqData);

        sendGameMessage(gameMember, gameMember.getRoomUuid(), EventTypeEnum.GAME_READY.getType());
        return modelMapper.map(gameMember, GameRoomMemberDto.class);
    }

    @Override
    public void exitGame(String userUuid, GameRoomParam gameRoomParam) {
        List<GameMember> gameMembers = gameMemberMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid(), gameRoomParam.getGameId());
        GameMember gameExist = gameMembers.stream().filter(o -> o.getUserUuid().equals(userUuid)).findFirst().orElse(null);
        if (gameExist == null) {
            throw new BsException(ErrorCode.BAD_REQUEST, "用户未加入游戏");
        }

        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid());
        if (gameRecord == null || gameRecord.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            throw new BsException(ErrorCode.BAD_REQUEST, "游戏房间不存在|已结束");
        }

        try {
            if (gameExist.getStatus() == GameMemberStatusEnum.READYING.getStatus()) {
                // 当用户在游戏准备中时，则取消准备，同时发送忽然玩家'离开游戏事件'
                UserOutReqData userOutReqData = UserOutReqData.builder().uid(userUuid).isCancelReady(true).build();
                sudService.pushGameEvent(PushEventEnum.USER_OUT.getEvent(), gameExist.getGameId(), String.valueOf(System.currentTimeMillis()), userOutReqData);
            } else if (gameExist.getStatus() == GameMemberStatusEnum.GAMING.getStatus()) {
                // 当用户正在游戏中时，则发送忽然'逃跑事件'
                GameEndReqData gameEndReqData = GameEndReqData.builder().uid(userUuid).roomId(gameRoomParam.getRoomUuid()).build();
                sudService.pushGameEvent(PushEventEnum.GAME_END.getEvent(), gameExist.getGameId(), String.valueOf(System.currentTimeMillis()), gameEndReqData);
            }
        } catch (Exception e) {
            log.info("调用忽然推送异常，用户{}，gameId:{}", userUuid, gameExist.getGameId());
        }

        gameExist.setStatus(GameMemberStatusEnum.GAME_LEAVE.getStatus());
        gameExist.setExitTime(System.currentTimeMillis());
        gameMemberMapperWrapper.updateByPrimaryKey(gameExist);

        sendGameMessage(gameExist, gameExist.getRoomUuid(), EventTypeEnum.GAME_LEAVE.getType());
    }

    @Override
    public GameRoomInfoDto getGameInfo(GameInfoParam gameInfoParam) {
        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(gameInfoParam.getRoomUuid());
        if (gameRecord == null || gameRecord.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            throw new BsException(ErrorCode.GAME_RECORD_NOT_EXIST, "游戏记录不存在或者已结束");
        }

        return GameRoomInfoDto.from(gameRecord);
    }

    @Override
    public GameRoomInfoDto getGameInfo(String roomUuid) {
        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(roomUuid);
        if (gameRecord == null) {
            return null;
        }

        return GameRoomInfoDto.from(gameRecord);
    }

    @Override
    public void startGame(String userUuid, GameRoomParam gameRoomParam) {
        String roomUuid = gameRoomParam.getRoomUuid();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        if (!userUuid.equals(liveRecordDto.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限开始游戏");
        }

        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(roomUuid);
        if (gameRecord == null || gameRecord.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            throw new BsException(ErrorCode.GAME_RECORD_NOT_EXIST, "游戏记录不存在或者已结束");
        }

        if(!gameRoomParam.getGameId().equals(gameRecord.getGameId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "游戏gameId不匹配");
        }

        if (gameRecord.getGameStatus() == GameStatusEnum.GAMING.getStatus()) {
            throw new BsException(ErrorCode.FORBIDDEN, "游戏已开始");
        }

        List<GameMember> gameMembers = gameMemberMapperWrapper.selectByGameRecordId(gameRecord.getId());
        if (gameMembers.stream().filter(o -> o.getStatus() == GameMemberStatusEnum.READYING.getStatus()).count() < 2) {
            throw new BsException(ErrorCode.FORBIDDEN, "参与人数不足，无法开始游戏");
        }

        List<QuickStartReqData.UserInfo> userInfos = gameMembers.stream()
                .filter(o -> o.getStatus() == GameMemberStatusEnum.READYING.getStatus())
                .map(o -> {
                    QuickStartReqData.UserInfo userInfo = new QuickStartReqData.UserInfo();
                    UserDto userDto = userService.getUser(o.getUserUuid());
                    userInfo.setUid(o.getUserUuid());
                    userInfo.setAvatarUrl(userDto.getIcon());
                    userInfo.setNickName(userDto.getUserName());
                    if (userDto.getSex() != null) {
                        userInfo.setGender(userDto.getSex() == 1 ? "male" : "female");
                    }
                    return userInfo;
                }).collect(Collectors.toList());

        QuickStartReqData quickStartReqData = QuickStartReqData.builder().roomId(gameRoomParam.getRoomUuid()).userInfos(userInfos).reportGameInfoKey(String.valueOf(gameRecord.getId())).build();
        sudService.pushGameEvent(PushEventEnum.QUICK_START.getEvent(), gameRecord.getGameId(), String.valueOf(System.currentTimeMillis()), quickStartReqData);

        // member更新状态为游戏中
        gameMembers.stream().filter(o -> o.getStatus() == GameMemberStatusEnum.READYING.getStatus()).forEach(o -> {
            o.setStatus(GameMemberStatusEnum.GAMING.getStatus());
            gameMemberMapperWrapper.updateByPrimaryKey(o);
        });

        // 游戏记录状态更新为正在游戏中
        gameRecord.setGameStatus(GameStatusEnum.GAMING.getStatus());
        gameRecordMapperWrapper.updateByPrimaryKey(gameRecord);

        // 发送NeRoom自定义聊天室消息：游戏开始事件
        sendGameMessage(gameRecord, gameRecord.getRoomUuid(), EventTypeEnum.GAME_START.getType());
    }

    @Override
    public void  endGame(String userUuid, GameRoomParam gameRoomParam, Boolean isSendMsg) {
        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid());
        if (gameRecord == null || gameRecord.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            throw new BsException(ErrorCode.GAME_RECORD_NOT_EXIST, "游戏记录不存在或者已结束");
        }
        if(!gameRoomParam.getGameId().equals(gameRecord.getGameId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "游戏gameId不匹配");
        }
        if(!userUuid.equals(gameRecord.getGameCreator())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限结束游戏");
        }

        if (gameRecord.getGameStatus() != GameStatusEnum.GAME_END.getStatus()) {
            RoomClearReqData roomClearReqData = RoomClearReqData.builder().roomId(gameRoomParam.getRoomUuid()).build();
            sudService.pushGameEvent(PushEventEnum.ROOM_CLEAR.getEvent(), gameRecord.getGameId(), String.valueOf(System.currentTimeMillis()), roomClearReqData);
        }

        gameRecord.setGameStatus(GameStatusEnum.GAME_END.getStatus());
        // 游戏记录及成员直接回收到history表中
        gameMemberMapperWrapper.deleteGameMember(gameRecord);
        gameRecordMapperWrapper.deleteGameRecord(gameRecord);

        if(Boolean.TRUE.equals(isSendMsg)) {
            sendGameMessage(gameRecord, gameRecord.getRoomUuid(), EventTypeEnum.GAME_END.getType());
        }
    }

    @Override
    public List<GameRoomMemberDto> getGameRoomMembers(String userUuid, GameRoomParam gameRoomParam) {
        GameRecord gameRecord = gameRecordMapperWrapper.selectByRoomUuid(gameRoomParam.getRoomUuid());
        if (gameRecord == null || gameRecord.getGameStatus() == GameStatusEnum.GAME_END.getStatus()) {
            throw new BsException(ErrorCode.GAME_RECORD_NOT_EXIST, "游戏记录不存在或者已结束");
        }
        if (!neRoomMemberService.userInNeRoom(gameRecord.getRoomArchiveId(), userUuid)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }

        List<GameMember> gameMembers = gameMemberMapperWrapper.selectByGameRecordId(gameRecord.getId());
        if (CollectionUtils.isEmpty(gameMembers)) {
            return Collections.emptyList();
        }
        return gameMembers.stream().map(o -> modelMapper.map(o, GameRoomMemberDto.class)).collect(Collectors.toList());
    }

    @Override
    public void statusReporter(String userUuid, GameRoomParam gameRoomParam) {
        userGameStatusWrapper.setGameStatus(gameRoomParam.getRoomUuid(), userUuid, GameMemberStatusEnum.GAMING.getStatus());
    }


    private String getLanguageCode() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.equals(Locale.CHINA)
                || locale.equals(Locale.CHINESE)
                || locale.equals(Locale.TRADITIONAL_CHINESE)) {
            return "zh-CN";
        } else {
            return "en-US";
        }
    }

    /**
     * 发送游戏信息消息到NeRoom
     *
     * @param data     游戏事件信息
     * @param roomUuid 房间uuid
     * @param type     消息事件
     */
    private void sendGameMessage(Object data, String roomUuid, Integer type) {
        EventDto eventDto = new EventDto(data, type);
        messageService.sendNeRoomChatMsg(roomUuid, eventDto);
    }

}
