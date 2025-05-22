package com.netease.nemo.controller.entertainmentLive;

import com.github.pagehelper.PageInfo;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.LiveIntroDto;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.enums.RoomProfileEnum;
import com.netease.nemo.entlive.enums.SeatModeEnum;
import com.netease.nemo.entlive.parameter.*;
import com.netease.nemo.entlive.service.EntLiveService;
import com.netease.nemo.entlive.util.LiveResourceUtil;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/nemo/entertainmentLive/live")
@Slf4j
@RestResponseBody
@TokenAuth
public class EntLiveController {

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private EntLiveService entLiveService;

    @Deprecated
    @RequestMapping("/createLive")
    public LiveIntroDto createLive(@Valid @RequestBody CreateLiveParam param) {
        checkCreateLiveParam(param);

        return lockerService.tryLockAndDoAndReturn(
                () -> entLiveService.createLiveRoom(param),
                RedisKeyEnum.ENT_CREATE_LIVE_ROOM_LOCK_KEY, Context.get().getUserUuid());
    }

    /**
     * 新增V3版本创建直播的接口
     * @param param
     * @return
     */
    @RequestMapping("/createLiveV3")
    public LiveIntroDto createLiveV3(@Valid @RequestBody CreateLiveParam param) {
        checkCreateLiveParam(param);

        return lockerService.tryLockAndDoAndReturn(
                () -> entLiveService.createLiveRoomV3(param),
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, Context.get().getUserUuid());
    }

    @RequestMapping(value = "/destroyLive")
    public void destroyLive(@Valid @RequestBody LiveParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "The Live Record Id Can Not Null");

        lockerService.tryLockAndDo(
                () -> entLiveService.closeLiveRoom(Context.get().getUserUuid(), param.getLiveRecordId()),
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, param.getLiveRecordId());
    }

    @RequestMapping(value = "/joinedLiveRoom")
    public void memberJoinedRoom(@Valid @RequestBody LiveParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        lockerService.tryLockAndDo(() -> {
            entLiveService.entryLiveRoom(Context.get().getUserUuid(), param.getLiveRecordId());
        }, RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, param.getLiveRecordId());

    }


    @RequestMapping(value = "/info")
    public LiveIntroDto getLiveInfo(@Valid @RequestBody LiveParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "Missing Required Parameter");

        return entLiveService.getLiveInfo(param.getLiveRecordId());
    }

    @PostMapping(value = "/list")
    public PageInfo<LiveIntroDto> getLiveList(@Valid @RequestBody LiveListQueryParam param) {
        checkLiveListParam(param);
        return entLiveService.getLiveRoomList(param);
    }

    @GetMapping(value = "/audience/list")
    public Object getAudienceList(@RequestParam("liveRecordId") Long liveRecordId,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", required = false, defaultValue = "200") Integer size) {
        AssertUtil.isTrue(page >= 1, ErrorCode.BAD_REQUEST, "PageNumber is invalid.");
        AssertUtil.isTrue(size >= 1 && size <= 200, ErrorCode.BAD_REQUEST, "PageSize is invalid or exceeds the maximum");
        return entLiveService.getAudienceList(liveRecordId, page, size);
    }

    /**
     * 暂停直播
     *
     * @param liveBaseParam 直播Id
     */
    @PostMapping(value = "/pauseLive")
    public void pauseLive(@RequestBody @Valid LiveBaseParam liveBaseParam) {
        String userUuid = Context.get().getUserUuid();
        lockerService.tryLockAndDo(
                () -> entLiveService.pauseLive(userUuid, liveBaseParam.getLiveRecordId()),
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, Context.get().getUserUuid());
    }

    /**
     * 恢复直播
     * @param liveBaseParam 直播Id
     */
    @PostMapping(value = "/resumeLive")
    public void resumeLive(@RequestBody @Valid LiveBaseParam liveBaseParam) {
        String userUuid = Context.get().getUserUuid();
        lockerService.tryLockAndDo(
                () -> entLiveService.resumeLive(userUuid, liveBaseParam.getLiveRecordId()),
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, Context.get().getUserUuid());
    }

    /**
     * 查询当前用户未结束的直播
     *
     */
    @GetMapping(value = "ongoing")
    public LiveIntroDto ongoingLive() {
        String userUuid = Context.get().getUserUuid();
        String appKey = Context.get().getAppKey();
        return entLiveService.getOngoingLive(appKey, userUuid);
    }

    private void checkLiveListParam(LiveListQueryParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getPageNum(), ErrorCode.BAD_REQUEST, "PageNumber Can Not Null");
        AssertUtil.notNull(param.getPageSize(), ErrorCode.BAD_REQUEST, "PageSize Can Not Null");
        if (!LiveTypeEnum.checkType(param.getLiveType())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "liveType enum error");
        }

        AssertUtil.isTrue(param.getPageNum() >= 1, ErrorCode.BAD_REQUEST, "PageNumber is invalid.");
        AssertUtil.isTrue(param.getPageSize() >= 1 && param.getPageSize() <= 50, ErrorCode.BAD_REQUEST, "PageSize is invalid or exceeds the maximum");
    }

    @GetMapping(value = "/getDefaultLiveInfo")
    public Object getDefaultLiveInfo() {
        return entLiveService.getDefaultLiveInfo();
    }


    @PostMapping(value = "/batch/reward")
    public void batchReward(@RequestBody LiveRewardParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getGiftId(), ErrorCode.BAD_REQUEST, "The Gift Id Can Not Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "The Live Record Id Can Not Null");

        param.setUserUuid(Context.get().getUserUuid());
        if (CollectionUtils.isEmpty(param.getTargets())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "the Targets is null");
        }

        lockerService.tryLockAndDo(
                () -> entLiveService.liveReward(param),
                RedisKeyEnum.ENT_LIVE_ROOM_LOCK_KEY, param.getLiveRecordId());
    }

    private void checkCreateLiveParam(CreateLiveParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        if (!LiveTypeEnum.checkType(param.getLiveType())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "liveType enum error");
        }
        if (StringUtils.isEmpty(param.getLiveTopic())) {
            param.setLiveTopic(LiveResourceUtil.getRandomEnTopic());
        }
        if (param.getSeatInviteMode() != null && null == SeatModeEnum.fromCode(param.getSeatInviteMode())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "seatInviteMode enum error");
        }
        if (param.getSeatApplyMode() != null && null == SeatModeEnum.fromCode(param.getSeatApplyMode())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "seatApplyMode enum error");
        }
        if (param.getSeatMode() != null && null == SeatModeEnum.fromCode(param.getSeatMode())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "seatMode enum error");
        }

        if (param.getSeatCount() != null && param.getSeatCount() > 20) {
            throw new BsException(ErrorCode.SEAT_COUNT_OVER_LIMIT);
        }

        if (!StringUtils.isEmpty(param.getExt()) && param.getExt().length() > 2048) {
            throw new BsException(ErrorCode.BAD_REQUEST, "The Ext Length Exceed Limit: 2048");
        }

        if (null != param.getRoomProfile() && RoomProfileEnum.fromCode(param.getRoomProfile()) == null) {
            throw new BsException(ErrorCode.BAD_REQUEST, "roomProfile enum error");
        }

    }
}
