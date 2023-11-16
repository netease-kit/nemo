package com.netease.nemo.controller.ktv;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.ChorusControlResultDto;
import com.netease.nemo.entlive.parameter.*;
import com.netease.nemo.entlive.service.ChorusService;
import com.netease.nemo.entlive.service.SingService;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.netease.nemo.enums.RedisKeyEnum.ENT_KTV_SING_LOCKER_KEY;

/**
 * KTV API
 *
 * @Author：CH
 * @Date：2023/10/11 10:19 AM
 */
@RestController
@RequestMapping("/nemo/entertainmentLive/ktv/")
@Slf4j
@RestResponseBody
@TokenAuth
public class KtvController {

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private SingService singService;

    @Resource
    private ChorusService chorusService;


    @PostMapping(value = "/sing/start")
    public void singStart(@Valid @RequestBody SingParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();
        String roomUuid = param.getRoomUuid();

        lockerService.tryLockAndDo(
                () -> singService.singStart(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, roomUuid);
    }

    @PostMapping(value = "sing/info")
    public Object getSingInfo(@Valid @RequestBody SingInfoParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        return singService.getSingInfo(param.getRoomUuid());
    }


    @PostMapping(value = "sing/action")
    public void singControl(@Valid @RequestBody SingActionParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();

        lockerService.tryLockAndDo(() -> singService.singControl(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }

    @PostMapping(value = "sing/chorus/invite")
    public ChorusControlResultDto chorusInvite(@Valid @RequestBody ChorusInviteParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();

        return (ChorusControlResultDto) lockerService.tryLockAndDoAndReturn(
                () -> chorusService.chorusInvite(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }

    @PostMapping(value = "sing/chorus/join")
    public ChorusControlResultDto chorusJoin(@Valid @RequestBody JoinChorusParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();

        return (ChorusControlResultDto) lockerService.tryLockAndDoAndReturn(
                () -> chorusService.joinChorus(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }

    @PostMapping(value = "sing/chorus/cancel")
    public ChorusControlResultDto chorusCancel(@Valid @RequestBody CancelChorusParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();

        return (ChorusControlResultDto) lockerService.tryLockAndDoAndReturn(
                () -> chorusService.cancelChorus(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }

    @PostMapping(value = "sing/chorus/ready")
    public ChorusControlResultDto chorusReady(@Valid @RequestBody FinishChorusReadyParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getChorusId(), ErrorCode.BAD_REQUEST, "The chorusId Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");


        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();

        return (ChorusControlResultDto) lockerService.tryLockAndDoAndReturn(
                () -> chorusService.chorusReady(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }

    @PostMapping(value = "sing/abandon")
    public void abandonSing(@Valid @RequestBody AbandonSingParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getOrderId(), ErrorCode.BAD_REQUEST, "The OrderId Can Not Null");
        AssertUtil.notNull(param.getRoomUuid(), ErrorCode.BAD_REQUEST, "The RoomUuid Can Not Null");

        String appKey = Context.get().getAppKey();
        String userUuid = Context.get().getUserUuid();
        lockerService.tryLockAndDo(
                () -> singService.abandonSing(userUuid, param),
                ENT_KTV_SING_LOCKER_KEY.getKeyPrefix(), appKey, param.getRoomUuid());
    }
}
