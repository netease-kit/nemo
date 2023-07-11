package com.netease.nemo.entlive.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.dto.message.RewardContentMessage;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.SeatModeEnum;
import com.netease.nemo.entlive.enums.SeatStatusEnum;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.entlive.model.po.LiveReward;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.entlive.util.LiveResourceUtil;
import com.netease.nemo.entlive.wrapper.LiveRecordWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.model.po.Gift;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.dto.neroom.CreateNeRoomDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.wrapper.GiftMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    private ModelMapper modelMapper;

    @Override
    public LiveDefaultInfoDto getDefaultLiveInfo() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.equals(Locale.CHINA)
                || locale.equals(Locale.CHINESE)
                || locale.equals(Locale.TRADITIONAL_CHINESE)) {
            return new LiveDefaultInfoDto(LiveResourceUtil.getRandomTopic(), LiveResourceUtil.getRandomPicture());
        } else {
            return new LiveDefaultInfoDto(LiveResourceUtil.getRandomEnTopic(), LiveResourceUtil.getRandomPicture());
        }
    }

    @Override
    @Transactional
    public LiveIntroDto createLiveRoom(CreateLiveParam param) {
        String host = Context.get().getUserUuid();
        UserDto userDto = userService.getUser(host);

        LiveRecord liveRecordExists = liveRecordWrapper.selectByUserUuidAndType(host, param.getLiveType());
        if (liveRecordExists != null) {
            return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), ObjectMapperUtil.map(liveRecordExists, LiveDto.class));
        }

        // TODO 使用分布式ID生成保证ID唯一
        String roomUuid = UUIDUtil.getUUID();
        if(StringUtils.isEmpty(param.getRoomName())) {
            param.setRoomName(param.getLiveTopic());
        }

        CreateNeRoomParam createNeRoomParam = buildCreateNeRoomParam(param, roomUuid);
        CreateNeRoomDto neRoomDto = neRoomService.createNeRoom(createNeRoomParam);

        LiveRecord liveRecord = LiveRecord.builderLiveRecord(param, host, roomUuid, neRoomDto.getRoomArchiveId());
        int res = liveRecordWrapper.insertSelective(liveRecord);
        if (res < 1) {
            log.error("addLiveRecord failed");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return new LiveIntroDto(BasicUserDto.buildBasicUser(userDto), ObjectMapperUtil.map(liveRecord, LiveDto.class));
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
        createNeRoomParam.setTemplateId(param.getConfigId() == null ? voiceRoomConfigId : param.getConfigId());

        CreateNeRoomParam.RoomSeatConfig roomSeatConfig = new CreateNeRoomParam.RoomSeatConfig();
        roomSeatConfig.setApplyMode(SeatModeEnum.fromCode(param.getSeatApplyMode()) == null ? 0 : param.getSeatApplyMode());
        roomSeatConfig.setInviteMode(SeatModeEnum.fromCode(param.getSeatInviteMode()) == null ? 0 : param.getSeatInviteMode());
        roomSeatConfig.setSeatCount(SeatModeEnum.fromCode(param.getSeatMode()) == null ? 0 : param.getSeatMode());
        // 当param.getSeatCount()<=0时 默认9个
        roomSeatConfig.setSeatCount(param.getSeatCount() <= 0 ? 9 : param.getSeatCount());
        createNeRoomParam.setRoomSeatConfig(roomSeatConfig);

        createNeRoomParam.setRoomConfig(new CreateNeRoomParam.RoomConfig(CreateNeRoomParam.ResourceConfig.buildEntVoiceRoom()));
        return createNeRoomParam;
    }

    @Override
    public LiveIntroDto getLiveInfo(Long liveRecordId) {
        LiveRecordDto liveRecordDto = liveRecordService.getLiveRecord(liveRecordId);
        if (null == liveRecordDto || !LiveEnum.isLive(liveRecordDto.getLive())) {
            throw new BsException(ErrorCode.ANCHOR_NOT_LIVING);
        }

        // 构造直播间信息
        LiveDto liveDto = ObjectMapperUtil.map(liveRecordDto, LiveDto.class);
        buildLiveDto(liveDto);

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

        // 删除NeRoom房间
        neRoomService.deleteNeRoom(liveRecord.getRoomArchiveId());

        // 标记直播间状态为结束
        liveRecordService.invalidLiveRecord(liveRecordId);

        // 清空点歌数据
        orderSongService.cleanOrderSongs(liveRecord.getId());
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
            buildLiveDto(liveDto);

            liveIntroDto.setLive(liveDto);

            UserDto userDto = userMap.get(liveDto.getUserUuid());
            if(userDto != null) {
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
     */
    private void buildLiveDto(LiveDto liveDto) {

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
     * @param liveRecordId     直播记录编号
     * @param neRoomSeats     麦位信息列表
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
