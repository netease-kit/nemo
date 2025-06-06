package com.netease.nemo.entlive.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.dto.message.RewardContentMessage;
import com.netease.nemo.entlive.enums.*;
import com.netease.nemo.entlive.event.GameRoomCloseEvent;
import com.netease.nemo.entlive.manager.LiveLayoutManager;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.entlive.model.po.LiveReward;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
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
import com.netease.nemo.openApi.dto.neroom.NeRoomMemberDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import com.netease.nemo.openApi.dto.neroom.UserOnSeatNotifyDto;
import com.netease.nemo.openApi.dto.nim.YunxinCreateLiveChannelDto;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParam;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParamV3;
import com.netease.nemo.openApi.paramters.neroom.StartLiveParam;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.netease.nemo.entlive.util.LiveResourceUtil.listenDefaultSeatCount;
import static com.netease.nemo.entlive.util.LiveResourceUtil.voiceDefaultSeatCount;
import static com.netease.nemo.enums.RedisKeyEnum.NE_ROOM_MEMBER_TABLE_KEY;

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

        if(LiveEnum.isLiveClose(liveRecord.getLive())){
            log.info("live is closed, liveRecordId:{}", liveRecordId);
            return;
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
    @Override
    public void entryLiveRoom(String userUuid, Long liveRecordId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null) {
            log.info("LiveRecord is valid");
            return;
        }

        String neRoomMemberTableKey = NE_ROOM_MEMBER_TABLE_KEY.getKeyPrefix() + liveRecordDto.getRoomArchiveId();

        NeRoomMemberDto.User user = neRoomService.getRoomMemberInfo(liveRecordDto.getRoomArchiveId(), userUuid);
        if (user == null) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }
        RoomMember member = new RoomMember(user.getRole(), user.getUserUuid());

        nemoRedisTemplate.opsForHash().put(neRoomMemberTableKey, member.getUserUuid(), member);

        // 如果用户是主播且直播间状态是'待直播'，则标记直播状态为为'直播中'
        if (member.getUserUuid().equals(liveRecordDto.getUserUuid()) && liveRecordDto.getLive().equals(LiveEnum.NOT_START.getCode())) {
            liveRecordService.updateLiveState(liveRecordId, LiveEnum.LIVE.getCode());
        }
        nemoRedisTemplate.expire(neRoomMemberTableKey, 7, TimeUnit.DAYS);
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

        Long chatRoomId = liveDto.getChatRoomId();
        // 获取观众人数
        long audienceCount = neRoomMemberService.getOnlineAudienceCount(chatRoomId);
        liveDto.setAudienceCount(audienceCount);

        // 获取打赏金额
        Long liveRewardTotal = liveRewardService.getLiveRewordTotal(liveDto.getId());
        liveDto.setRewardTotal(liveRewardTotal);

        // 获取上麦人数
        List<NeRoomSeatDto> neRoomSeats = neRoomService.getNeRoomSeatList(liveDto.getRoomArchiveId());
        liveDto.setOnSeatCount(neRoomSeats.stream().filter(o -> o.getStatus() == SeatStatusEnum.ON_SEAT.getStatus()).count());

        // 设置麦上主播打赏总数
        liveDto.setSeatUserReward(getSeatUserReward(liveDto.getId(), neRoomSeats));

        if (null != liveDto.getLiveConfig()) {
            CreateNeRoomParam.ExternalLiveConfig externalLiveConfig = GsonUtil.fromJson(liveDto.getLiveConfig(), CreateNeRoomParam.ExternalLiveConfig.class);
            if (!isHost && externalLiveConfig != null) {
                externalLiveConfig.setPushUrl(null);
            }
            liveDto.setExternalLiveConfig(externalLiveConfig);
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

    @Override
    public LiveIntroDto createLiveRoomV3(CreateLiveParam param) {
        String host = Context.get().getUserUuid();
        UserDto userDto = userService.getUser(host);

        LiveRecord liveRecordExists = liveRecordWrapper.selectByUserUuidAndType(host, param.getLiveType());
        if (liveRecordExists != null) {
            // 如果已经有直播记录，则先关闭原来的历史直播
            closeLiveRoom(host, liveRecordExists.getId());
        }

        // TODO 使用分布式ID生成保证ID唯一
        String roomUuid = UUIDUtil.getUUID();
        if (StringUtils.isEmpty(param.getRoomName())) {
            param.setRoomName(param.getLiveTopic());
        }

        // 创建V3参数对象
        CreateNeRoomParamV3 createNeRoomParamV3 = new CreateNeRoomParamV3();
        createNeRoomParamV3.setRoomUuid(roomUuid);
        createNeRoomParamV3.setRoomName(param.getRoomName());
        createNeRoomParamV3.setHostUserUuid(host);

        CreateNeRoomParamV3.RoomComponentConfigV3 config = new CreateNeRoomParamV3.RoomComponentConfigV3();
        // 设置麦位配置
        if (param.getSeatCount() != null && param.getSeatCount() > 0) {
            CreateNeRoomParamV3.RoomComponentConfigV3.SeatConfig seatConfig = new CreateNeRoomParamV3.RoomComponentConfigV3.SeatConfig();
            seatConfig.setEnable(true);
            seatConfig.setSeatCount(param.getSeatCount());
            seatConfig.setApplyMode(param.getSeatApplyMode());
            seatConfig.setInviteMode(param.getSeatInviteMode());
            config.setSeat(seatConfig);
        }

        // 设置房间类型配置
        if (param.getRoomProfile() != null) {
            RoomProfileEnum roomProfileEnum = RoomProfileEnum.fromCode(param.getRoomProfile());
            assert roomProfileEnum != null;
            createNeRoomParamV3.setRoomProfile(roomProfileEnum.getState());
        }

        if(LiveTypeEnum.isPkLive(param.getLiveType())){
            // 直播场景强制指定此模式
            createNeRoomParamV3.setRoomProfile(RoomProfileEnum.LIVE_BROADCASTING.getState());
        }

        // 设置模板ID
        Long configId = getDefaultTemplateId(param.getLiveType());
        createNeRoomParamV3.setTemplateId(configId);

        // 设置其他组件配置 (根据RoomResourceConfig来适配)
        config.setChatroom(true); // 默认开启聊天室
        config.setRtc(true);      // 默认开启RTC

        createNeRoomParamV3.setConfig(config);

        // 设置直播配置
        if(LiveTypeEnum.isPkLive(param.getLiveType())) {
            CreateNeRoomParamV3.RoomComponentConfigV3.LiveConfig liveConfig = new CreateNeRoomParamV3.RoomComponentConfigV3.LiveConfig();
            liveConfig.setEnable(true);
            config.setLive(liveConfig);
        }

        CreateNeRoomDto neRoomDto = neRoomService.createNeRoomV3(createNeRoomParamV3);

        LiveRecord liveRecord = LiveRecord.builderLiveRecordV3(param, host, roomUuid, neRoomDto);
        int res = liveRecordWrapper.insertSelective(liveRecord);
        if (res < 1) {
            log.error("addLiveRecord failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), ObjectMapperUtil.map(liveRecord, LiveDto.class));
    }

    public PageDto<AudienceInfo> getAudienceList(Long liveRecordId, Integer page, Integer size) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (liveRecordDto == null) {
            throw new BsException(ErrorCode.LIVE_RECORD_NOT_EXIST);
        }

        List<AudienceInfo> onlineAudienceList = neRoomMemberService.getOnlineAudienceList(liveRecordDto.getChatRoomId(), page, size);
        List<String> collect = onlineAudienceList
                .stream().map(AudienceInfo::getUserUuid).collect(Collectors.toList());

        Map<String, UserDto> users = userService.getUsers(collect)
                .stream().collect(Collectors.toMap(UserDto::getUserUuid, o -> o));

        Long onlineAudienceCount = neRoomMemberService.getOnlineAudienceCount(liveRecordDto.getChatRoomId());

        onlineAudienceList.forEach(au -> {
            UserDto userDto = users.get(au.getUserUuid());
            if(userDto != null) {
                au.setIcon(userDto.getIcon());
                au.setUserName(userDto.getUserName());
            }
        });

        return new PageDto<>(onlineAudienceCount, onlineAudienceList);
    }

    @Override
    public LiveIntroDto getOngoingLive(String appKey, String userUuid) {
        List<Integer> ongoingState = Lists.newArrayList(LiveEnum.NOT_START.getCode(), LiveEnum.LIVE.getCode(), LiveEnum.LIVE_PAUSE.getCode());
        LiveRecordDto liveRecord = liveRecordService.getLiveRecord(userUuid, ongoingState);
        if(liveRecord == null){
            return null;
        }
        return getLiveInfo(liveRecord.getId());
    }

    @Override
    public void updatePkLiveLayout(Long liveRecordId) {

        log.info("updateLiveWhenUserOnSeat, liveRecordId:{}", liveRecordId);
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (null == liveRecord) {
            log.info("LiveRecord is valid:{}", liveRecordId);
            throw new BsException(ErrorCode.LIVE_RECORD_NOT_EXIST);
        }

        if(!LiveTypeEnum.isPkLive(liveRecord.getLiveType())){
            // 不是互动直播，不需要发起推流任务
            return;
        }

        // 获取所有麦上成员
        List<String> allSeatUsers = liveRecordService.getAllSeatUsersTyped(liveRecord.getRoomArchiveId(), liveRecord.getUserUuid())
                .stream().map(UserOnSeatNotifyDto.SeatUser::getUserUuid).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(allSeatUsers)){
            // 直播间没有人了
            pauseLive(liveRecord.getUserUuid(), liveRecordId);
            return;
        }else if(allSeatUsers.size() == 1 && allSeatUsers.contains(liveRecord.getUserUuid())){
            // 直播间主播进入了
            if(LiveEnum.isPause(liveRecord.getLive())){
                // 如果之前是暂停状态，则恢复直播
                resumeLive(liveRecord.getUserUuid(), liveRecordId);
                return;
            }else {
                // 更新直播状态为直播中
                log.info("update Live status WhenUserOnSeat, liveRecordId:{}, allSeatUsers:{}", liveRecordId, allSeatUsers);
                liveRecord.setLive(LiveEnum.LIVE.getCode());
                liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
            }
        }else {
            log.info("update LiveSeat status WhenUserOnSeat, liveRecordId:{}, allSeatUsers:{}", liveRecordId, allSeatUsers);
            liveRecord.setLive(LiveEnum.LIVE_SEAT.getCode());
            liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
        }
        // 开启推流任务
        StartLiveParam startLiveParam = LiveLayoutManager.buildLayoutParam(liveRecord.getRoomArchiveId(), allSeatUsers , liveRecord.getLiveTopic());
        try {
            // 尝试更新推流任务，失败后则重启推流任务
            neRoomService.updateLive(startLiveParam);
        }catch (Exception e){
            neRoomService.startLive(startLiveParam);
        }
    }

    @Override
    public void pauseLive(String operator, Long liveRecordId) {
        log.info("pauseLive operator:{}, liveRecordId:{}", operator, liveRecordId);
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (!operator.equals(liveRecord.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限暂停直播");
        }

        if(LiveEnum.isPause(liveRecord.getLive())){
            log.info("live is paused, liveRecordId:{}", liveRecordId);
            return;
        }

        if(!LiveTypeEnum.isPkLive(liveRecord.getLive())){
            throw new BsException(ErrorCode.FORBIDDEN, "只有普通直播才能暂停");
        }

        if(!Objects.equals(liveRecord.getLive(), LiveEnum.LIVE.getCode())){
            throw new BsException(ErrorCode.FORBIDDEN, "非直播中状态，不能暂停");
        }

        liveRecord.setLive(LiveEnum.LIVE_PAUSE.getCode());
        liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
        // 发送直播暂停事件
        LiveRecordDto liveRecordDto = ObjectMapperUtil.map(liveRecord, LiveRecordDto.class);
        messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(liveRecordDto, EventTypeEnum.LIVE_PAUSE.getType()));
        // 停止推流任务
        neRoomService.stopLive(liveRecord.getRoomArchiveId());
    }

    @Override
    public void resumeLive(String operator, Long liveRecordId) {
        log.info("resumeLive operator:{}, liveRecordId:{}", operator, liveRecordId);
        LiveRecord liveRecord = liveRecordWrapper.selectByPrimaryKey(liveRecordId);
        if (!operator.equals(liveRecord.getUserUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户无权限恢复直播");
        }

        if(!LiveTypeEnum.isPkLive(liveRecord.getLiveType())){
            throw new BsException(ErrorCode.FORBIDDEN, "无法恢复，类型不支持");
        }

        if(!Objects.equals(liveRecord.getLive(), LiveEnum.LIVE_PAUSE.getCode())){
            throw new BsException(ErrorCode.FORBIDDEN, "直播状态不正确，无法恢复直播");
        }

        liveRecord.setLive(LiveEnum.LIVE.getCode());
        liveRecordWrapper.updateByPrimaryKeySelective(liveRecord);
        // 发送直播恢复事件
        LiveRecordDto liveRecordDto = ObjectMapperUtil.map(liveRecord, LiveRecordDto.class);
        messageService.sendNeRoomChatMsg(liveRecordDto.getRoomUuid(), new EventDto(liveRecordDto, EventTypeEnum.LIVE_RESUME.getType()));
        StartLiveParam startLiveParam = LiveLayoutManager.buildLayoutParam(liveRecord.getRoomArchiveId(), Collections.singletonList(liveRecord.getUserUuid()), liveRecord.getRoomName());
        neRoomService.startLive(startLiveParam);
    }
}
