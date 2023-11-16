package com.netease.nemo.entlive.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.dto.message.RewardContentMessage;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.enums.SeatModeEnum;
import com.netease.nemo.entlive.enums.SeatStatusEnum;
import com.netease.nemo.entlive.event.GameRoomCloseEvent;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.entlive.model.po.LiveReward;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.entlive.util.LiveResourceUtil;
import com.netease.nemo.entlive.wrapper.LiveRecordWrapper;
import com.netease.nemo.entlive.wrapper.SingRedisWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.model.po.Gift;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.neroom.CreateNeRoomDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import com.netease.nemo.openApi.dto.nim.YunxinCreateLiveChannelDto;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParam;
import com.netease.nemo.queue.producer.DelayQueueProducer;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import com.netease.nemo.wrapper.GiftMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.netease.nemo.entlive.util.LiveResourceUtil.listenDefaultSeatCount;
import static com.netease.nemo.entlive.util.LiveResourceUtil.voiceDefaultSeatCount;

@Service
@Slf4j
public class EntLiveServiceImpl implements EntLiveService {

    @Resource
    private UserService userService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private MessageService messageService;

    @Resource
    private GiftMapperWrapper giftMapperWrapper;

    @Value("${business.voiceRoomConfigId}")
    private Long voiceRoomConfigId;

    @Value("${business.listenTogetherConfigId}")
    private Long listenTogetherConfigId;

    @Value("${business.pkConfigId}")
    private Long pkConfigId;

    @Resource
    private LiveRecordWrapper liveRecordWrapper;

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private LiveRewardServiceImpl liveRewardService;

    @Resource
    private OrderSongService orderSongService;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private NimService nimService;

    @Resource
    private ModelMapper modelMapper;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;
    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Resource
    private MusicPlayService musicPlayService;

    @Value("${business.gameRoomConfigId}")
    private Long gameRoomConfigId;

    @Resource
    private SingRedisWrapper singRedisWrapper;

    @Resource
    private DelayQueueProducer delayQueueProducer;

    @Resource
    private ApplicationEventPublisher publisher;

    @Override
    public LiveDefaultInfoDto getDefaultLiveInfo() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.equals(Locale.CHINA)
                || locale.equals(Locale.CHINESE)
                || locale.equals(Locale.TRADITIONAL_CHINESE)) {
            return new LiveDefaultInfoDto(LiveResourceUtil.getRandomTopic(), LiveResourceUtil.getRandomPicture(), LiveResourceUtil.defaultPicUrlList);
        } else {
            return new LiveDefaultInfoDto(LiveResourceUtil.getRandomEnTopic(), LiveResourceUtil.getRandomPicture(), LiveResourceUtil.defaultPicUrlList);
        }
    }

    @Override
    @Transactional
    public LiveIntroDto createLiveRoom(CreateLiveParam param) {
        String host = Context.get().getUserUuid();
        UserDto userDto = userService.getUser(host);
        LiveRecord liveRecordExists = liveRecordWrapper.selectByUserUuidAndType(host, param.getLiveType());
        if (liveRecordExists != null) {
            return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), LiveDto.fromLiveRecord(liveRecordExists));
        }

        // TODO 使用分布式ID生成保证ID唯一
        String roomUuid = UUIDUtil.getUUID();
        if (StringUtils.isEmpty(param.getRoomName())) {
            param.setRoomName(param.getLiveTopic());
        }

        CreateNeRoomParam createNeRoomParam = buildCreateNeRoomParam(param, roomUuid);
        YunxinCreateLiveChannelDto yunxinCreateLiveChannelDto = null;
        if (LiveTypeEnum.isPkLive(param.getLiveType())) {
            yunxinCreateLiveChannelDto = nimService.createLive(roomUuid + System.currentTimeMillis(), null);
            createNeRoomParam.setExternalLiveConfig(CreateNeRoomParam.ExternalLiveConfig.toExternalLiveConfig(yunxinCreateLiveChannelDto));
        }

        CreateNeRoomDto neRoomDto = neRoomService.createNeRoom(createNeRoomParam);
        LiveRecord liveRecord = LiveRecord.builderLiveRecord(param, host, roomUuid, neRoomDto.getRoomArchiveId(), yunxinCreateLiveChannelDto);
        int res = liveRecordWrapper.insertSelective(liveRecord);
        if (res < 1) {
            log.error("addLiveRecord failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), LiveDto.fromLiveRecord(liveRecord));
    }


    /**
     * 构造CreateNeRoomParam对象
     *
     * @param param    CreateLiveParam入参
     * @param roomUuid NeRoom roomUuid
     * @return CreateNeRoomParam
     */
    private CreateNeRoomParam buildCreateNeRoomParam(CreateLiveParam param, String roomUuid) {
        CreateNeRoomParam createNeRoomParam = new CreateNeRoomParam();
        createNeRoomParam.setRoomName(param.getRoomName());
        createNeRoomParam.setRoomUuid(roomUuid);

        Integer seatCount =  param.getSeatCount();
        // 如果configId为空，则使用默认配置id
        createNeRoomParam.setTemplateId(param.getConfigId() != null ? param.getConfigId() : getDefaultTemplateId(param.getLiveType()));

        CreateNeRoomParam.RoomSeatConfig roomSeatConfig = new CreateNeRoomParam.RoomSeatConfig();
        roomSeatConfig.setApplyMode(SeatModeEnum.fromCode(param.getSeatApplyMode()) == null ? 0 : param.getSeatApplyMode());
        roomSeatConfig.setInviteMode(SeatModeEnum.fromCode(param.getSeatInviteMode()) == null ? 0 : param.getSeatInviteMode());
        roomSeatConfig.setSeatCount(seatCount == null || seatCount <= 0 ? getDefaultSeatCount(param.getLiveType()) : seatCount);
        createNeRoomParam.setRoomSeatConfig(roomSeatConfig);
        if (!StringUtils.isEmpty(param.getExt())) {
            createNeRoomParam.setExt(param.getExt());
        }

        createNeRoomParam.setRoomConfig(new CreateNeRoomParam.RoomConfig(CreateNeRoomParam.ResourceConfig.buildEntVoiceRoom()));
        createNeRoomParam.setRoomProfile(param.getRoomProfile());
        return createNeRoomParam;
    }

    /**
     * 获取默认麦位数 默认为语聊房的麦位数9个
     *
     * @param liveType 直播类型
     * @return 默认麦位数
     */
    private Integer getDefaultSeatCount(Integer liveType) {
        if (LiveTypeEnum.LISTEN_TOGETHER.getType() == liveType) {
            return listenDefaultSeatCount;
        } else if (LiveTypeEnum.CHAT.getType() == liveType) {
            return voiceDefaultSeatCount;
        }
        return voiceDefaultSeatCount;
    }

    /**
     * 默认配置id 默认拿语聊房模板id
     *
     * @param liveType 直播类型
     * @return 默认配置id
     */
    private Long getDefaultTemplateId(Integer liveType) {
        if (LiveTypeEnum.LISTEN_TOGETHER.getType() == liveType) {
            return listenTogetherConfigId;
        } else if (LiveTypeEnum.CHAT.getType() == liveType) {
            return voiceRoomConfigId;
        } else if (LiveTypeEnum.INTERACTION_LIVE_CROSS_CHANNEL.getType() == liveType) {
            return pkConfigId;
        } else if (LiveTypeEnum.GAME_ROOM.getType() == liveType) {
            return gameRoomConfigId;
        }

        return voiceRoomConfigId;
    }

    @Override
    public LiveIntroDto getLiveInfo(Long liveRecordId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (null == liveRecordDto || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        Boolean isHost = Context.get().getUserUuid().equals(liveRecordDto.getUserUuid());
        // 构造直播间信息
        LiveDto liveDto = ObjectMapperUtil.map(liveRecordDto, LiveDto.class);
        buildLiveDto(liveDto, isHost);

        UserDto userDto = userService.getUser(liveRecordDto.getUserUuid());
        return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), liveDto);
    }

    @Override
    @Transactional
    public void closeLiveRoom(String operator, Long liveRecordId) {
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (!operator.equals(liveRecord.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限关播");
        }

        if (LiveTypeEnum.isPkLive(liveRecord.getLiveType())) {
            JsonObject live = GsonUtil.parseJsonObject(liveRecord.getLiveConfig());
            nimService.deleteLiveChannel(live.get("cid").getAsString());
        }

        if (LiveTypeEnum.isKTVLive(liveRecord.getLiveType())) {
            // 房间结束清空ktv房间演唱信息
            singRedisWrapper.delSingBaseInfo(liveRecord.getRoomUuid());
        }

        // 删除NeRoom房间
        neRoomService.deleteNeRoom(liveRecord.getRoomArchiveId());

        // 标记直播间状态为结束
        liveRecordService.invalidLiveRecord(liveRecordId);

        // 清空点歌数据
        orderSongService.cleanOrderSongs(liveRecordId);

        // 清空当前播放歌曲信息
        musicPlayService.cleanPlayerMusicInfo(liveRecordId);

        // 发送房间结束通知
        GameRoomCloseEvent gameRoomCloseTask = new GameRoomCloseEvent(liveRecord.getRoomUuid());
        publisher.publishEvent(gameRoomCloseTask);
    }

    @Override
    public PageInfo<LiveIntroDto> getLiveRoomList(LiveListQueryParam param) {
        int pageNum = Optional.ofNullable(param.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(param.getPageSize()).orElse(20);

        String excludeUserUuid = Context.get().getUserUuid();
        Page<LiveRecord> liveRecordPage = PageHelper.startPage(pageNum, pageSize).doSelectPage(() ->
                liveRecordService.getLivingRecords(param.getLiveType(), excludeUserUuid));

        if (CollectionUtils.isEmpty(liveRecordPage.getResult())) {
            return liveRecordPage.toPageInfo(s -> new LiveIntroDto());
        }

        List<UserDto> users = userService.getUsers(liveRecordPage.getResult().stream().map(LiveRecord::getUserUuid).collect(Collectors.toList()));
        Map<String, UserDto> userMap = users.stream().collect(Collectors.toMap(UserDto::getUserUuid, o -> o));

        PageInfo<LiveIntroDto> rtn = liveRecordPage.toPageInfo(s -> {
            LiveIntroDto liveIntroDto = new LiveIntroDto();

            LiveDto liveDto = modelMapper.map(s, LiveDto.class);
            Boolean isHost = Context.get().getUserUuid().equals(s.getUserUuid());
            buildLiveDto(liveDto, isHost);

            liveIntroDto.setLive(liveDto);

            UserDto userDto = userMap.get(liveDto.getUserUuid());
            if (userDto != null) {
                liveIntroDto.setAnchor(BasicUserDto.buildBasicUser(userDto));
            }
            return liveIntroDto;
        });
        rtn.setTotal(liveRecordPage.getTotal());
        return rtn;
    }

    @Override
    @Transactional
    public void liveReward(LiveRewardParam liveRewardParam) {
        List<String> targets = liveRewardParam.getTargets();
        Long giftId = liveRewardParam.getGiftId();
        String userUuid = liveRewardParam.getUserUuid();
        Integer giftCount = liveRewardParam.getGiftCount();
        Long liveRecordId = liveRewardParam.getLiveRecordId();

        if (CollectionUtils.isEmpty(targets)) {
            return;
        }

        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (null == liveRecordDto || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }
        Gift gift = giftMapperWrapper.selectByPrimaryKey(giftId);
        List<LiveReward> liveRewards = targets.stream().map(o -> buildLiveReward(userUuid, o, liveRecordDto, gift, giftCount)).collect(Collectors.toList());
        liveRewardService.batchInsertLiveReward(liveRewards);

        // TODO
        // 被打赏者账户加礼物对应的云币
        // 打赏者账号减礼物对应的云币

        // 获取连麦用户打赏信息
        List<SeatUserRewardInfoDto> seatUserReward = getSeatUserReward(liveRecordId, liveRecordDto.getRoomArchiveId());

        // 发送打赏消息
        RewardContentMessage rewardContentMessage = buildRewardMessage(userUuid, targets, gift, giftCount, liveRecordDto, seatUserReward);
        messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(rewardContentMessage, EventTypeEnum.ENT_USER_REWARD.getType()));
    }

    /**
     * 构造打赏消息事件
     *
     * @param userUuid       打赏者
     * @param targets        被打赏者列表
     * @param gift           礼物对象
     * @param giftCount      礼物个数
     * @param liveRecordDto  直播详情
     * @param seatUserReward 麦位观众打赏总数
     * @return RewardContent
     */
    private RewardContentMessage buildRewardMessage(String userUuid, List<String> targets, Gift gift, Integer giftCount, LiveRecordDto liveRecordDto, List<SeatUserRewardInfoDto> seatUserReward) {
        RewardContentMessage rewardContentMessage = new RewardContentMessage();

        rewardContentMessage.setSenderUserUuid(userUuid);
        rewardContentMessage.setGiftId(gift.getId());
        rewardContentMessage.setGiftCount(giftCount);
        rewardContentMessage.setSendTime(System.currentTimeMillis());
        rewardContentMessage.setSeatUserReward(seatUserReward);

        // 设置打赏者信息
        UserDto rewardUser = userService.getUser(userUuid);
        rewardContentMessage.setUserUuid(userUuid);
        rewardContentMessage.setUserName(rewardUser.getUserName());

        // 设置被打赏者targets信息
        List<UserDto> targetUsers = userService.getUsers(targets);
        rewardContentMessage.setTargets(targetUsers.stream().map(BasicUserDto::buildBasicUser).collect(Collectors.toList()));
        return rewardContentMessage;
    }

    /**
     * @param userUuid      打赏者 userUuid
     * @param target        被打赏者 userUuid
     * @param liveRecordDto 直播间信息
     * @param gift          礼物对象
     * @param giftCount     礼物个数
     * @return LiveReward
     */
    private LiveReward buildLiveReward(String userUuid, String target, LiveRecordDto liveRecordDto, Gift gift, Integer giftCount) {
        return LiveReward.buildReward(liveRecordDto.getId(), liveRecordDto.getRoomArchiveId(), liveRecordDto.getRoomUuid(), userUuid, target, gift, giftCount);
    }


    /**
     * 构造直播间信息 NeRoom房间人数和打赏总数
     *
     * @param liveDto liveDto直播信息对象
     * @param isHost  是否主播操作
     */
    private void buildLiveDto(LiveDto liveDto, Boolean isHost) {

        // 获取NeRoom人数
        long neRoomMemberSize = neRoomMemberService.getRoomMemberSize(liveDto.getRoomArchiveId());
        liveDto.setAudienceCount((neRoomMemberSize <= 1) ? 0L : neRoomMemberSize - 1);

        // 获取打赏金额
        Long liveRewardTotal = liveRewardService.getLiveRewordTotal(liveDto.getId());
        liveDto.setRewardTotal(liveRewardTotal);

        // 获取上麦人数
        List<NeRoomSeatDto> neRoomSeats = neRoomService.getNeRoomSeatList(liveDto.getRoomArchiveId());
        liveDto.setOnSeatCount(neRoomSeats.stream().filter(o -> o.getStatus() == SeatStatusEnum.ON_SEAT.getStatus()).count());

        // 设置麦上主播打赏总数
        liveDto.setSeatUserReward(getSeatUserReward(liveDto.getId(), neRoomSeats));

        if (null != liveDto.getLiveConfig()) {
            YunxinCreateLiveChannelDto yunxinCreateLiveChannelDto = GsonUtil.fromJson(liveDto.getLiveConfig(), YunxinCreateLiveChannelDto.class);
            if (!isHost) {
                yunxinCreateLiveChannelDto.setPushUrl(null);
            }
            liveDto.setExternalLiveConfig(CreateNeRoomParam.ExternalLiveConfig.toExternalLiveConfig(yunxinCreateLiveChannelDto));
        }
    }

    /**
     * 获取麦位用户总打赏数
     *
     * @param liveRecordId  直播记录编号
     * @param roomArchiveId NeRoom房间编号
     */
    private List<SeatUserRewardInfoDto> getSeatUserReward(Long liveRecordId, String roomArchiveId) {
        List<NeRoomSeatDto> neRoomSeats = neRoomService.getNeRoomSeatList(roomArchiveId);
        if (CollectionUtils.isEmpty(neRoomSeats)) {
            return Collections.emptyList();
        }

        return getSeatUserReward(liveRecordId, neRoomSeats);
    }


    /**
     * 获取麦位用户总打赏数
     *
     * @param liveRecordId 直播记录编号
     * @param neRoomSeats  麦位信息列表
     */
    private List<SeatUserRewardInfoDto> getSeatUserReward(Long liveRecordId, List<NeRoomSeatDto> neRoomSeats) {
        if (CollectionUtils.isEmpty(neRoomSeats)) {
            return Collections.emptyList();
        }

        return neRoomSeats.stream()
                .filter(o -> SeatStatusEnum.ON_SEAT.getStatus() == o.getStatus())
                .map(o -> {
                    SeatUserRewardInfoDto seatUserRewardInfo = new SeatUserRewardInfoDto();
                    NeRoomSeatDto.SeatUser seatUser = o.getUser();
                    if (seatUser != null) {
                        seatUserRewardInfo.setSeatIndex(o.getIndex());
                        seatUserRewardInfo.setUserUuid(seatUser.getUserUuid());
                        seatUserRewardInfo.setIcon(seatUser.getIcon());
                        seatUserRewardInfo.setUserName(seatUser.getName());
                        LiveRewardTotalDto liveRewardTotalDto = liveRewardService.countUserRewardTotal(liveRecordId, seatUser.getUserUuid());
                        Long rewardTotal = 0L;
                        if (liveRewardTotalDto != null) {
                            rewardTotal = liveRewardTotalDto.getRewardTotal();
                        }
                        seatUserRewardInfo.setRewardTotal(rewardTotal);
                    }
                    return seatUserRewardInfo;
                }).collect(Collectors.toList());
    }
}
