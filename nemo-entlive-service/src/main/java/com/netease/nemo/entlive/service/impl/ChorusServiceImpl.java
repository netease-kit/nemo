package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.dto.EventDto;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.delay.DelayTopic;
import com.netease.nemo.entlive.delay.task.JoinChorusTask;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.enums.ChorusStateEnum;
import com.netease.nemo.entlive.enums.ChorusTypeEnum;
import com.netease.nemo.entlive.enums.KtvSingStatusEnum;
import com.netease.nemo.entlive.enums.OrderSongStatusEnum;
import com.netease.nemo.entlive.model.po.ChorusRecord;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.entlive.parameter.CancelChorusParam;
import com.netease.nemo.entlive.parameter.ChorusInviteParam;
import com.netease.nemo.entlive.parameter.FinishChorusReadyParam;
import com.netease.nemo.entlive.parameter.JoinChorusParam;
import com.netease.nemo.entlive.service.*;
import com.netease.nemo.entlive.wrapper.OrderSongMapperWrapper;
import com.netease.nemo.entlive.wrapper.SingRedisWrapper;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.queue.producer.DelayQueueProducer;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author：CH
 * @name：caohao
 * @Date：2023/10/13 10:54 AM
 */
@Service
@Slf4j
public class ChorusServiceImpl implements ChorusService {

    @Resource
    private LiveRecordService liveRecordService;

    @Resource
    private ChorusRecordService chorusRecordService;

    @Resource
    private OrderSongService orderSongService;

    @Resource
    private OrderSongMapperWrapper orderSongMapperWrapper;

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private SingRedisWrapper singRedisWrapper;

    @Resource
    private DelayQueueProducer delayQueueProducer;


    @Override
    public ChorusControlResultDto chorusInvite(String operator, ChorusInviteParam param) {
        String roomUuid = param.getRoomUuid();
        Long orderId = param.getOrderId();
        Map<String, Object> deviceParam = param.getDeviceParam();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        if (!neRoomMemberService.isUserOnSeat(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户不在麦上");
        }

        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);

        if (singBaseInfo == null || !singBaseInfo.getOrderId().equals(orderId)) {
            throw new BsException(ErrorCode.FORBIDDEN, "当前演唱歌曲信息不正确");
        }
        if (KtvSingStatusEnum.NOT_SING.getStatus() != singBaseInfo.getSongStatus()) {
            throw new BsException(ErrorCode.SING_PROCESSING);
        }

        OrderSong orderSong = orderSongService.getAndCheckKtvOrderSong(operator, orderId);

        //创建合唱记录
        String chorusId = UUIDUtil.getRandomNumber(5) + System.currentTimeMillis();
        ChorusRecord chorusRecord = new ChorusRecord(chorusId, liveRecordDto, operator, orderId, ChorusStateEnum.INVITING.getCode());
        if (deviceParam != null && !deviceParam.isEmpty()) {
            chorusRecord.setLeaderDeviceParam(GsonUtil.toJsonNoDouble(deviceParam));
        }
        chorusRecordService.addChorusRecord(chorusRecord);


        // 设置歌曲状态为合唱邀请中
        singBaseInfo.setSongStatus(KtvSingStatusEnum.INVITING.getStatus());
        singBaseInfo.setChorusId(chorusId);
        singBaseInfo.setOrderId(orderId);
        singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

        // 返回对象
        ChorusControlResultDto chorusControlResultDto = buildChorusResult(chorusRecord, orderSong, liveRecordDto);

        //发送合唱邀请消息
        UserDto userDto = userService.getUser(operator);
        ChorusBroadcastResultDto broadcastResultDto = new ChorusBroadcastResultDto(chorusControlResultDto, BasicUserDto.buildBasicUser(userDto));
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(broadcastResultDto, EventTypeEnum.INVITE_CHORUS.getType()));

        return chorusControlResultDto;
    }

    @Override
    public ChorusControlResultDto joinChorus(String operator, JoinChorusParam param) {
        String roomUuid = param.getRoomUuid();
        Map<String, Object> deviceParam = param.getDeviceParam();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        if (!neRoomMemberService.userInNeRoom(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.USER_NOT_IN_ROOM, "用户未加入房间");
        }
        if (!neRoomMemberService.isUserOnSeat(liveRecordDto.getRoomArchiveId(), operator)) {
            throw new BsException(ErrorCode.FORBIDDEN, "用户不在麦上");
        }

        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null || !singBaseInfo.getChorusId().equals(param.getChorusId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "当前合唱信息不正确");
        }
        if (singBaseInfo.getSongStatus() != KtvSingStatusEnum.INVITING.getStatus()) {
            throw new BsException(ErrorCode.SING_PROCESSING);
        }

        ChorusRecord chorusRecord = chorusRecordService.getChorusRecordByChorusId(param.getChorusId());
        if (ChorusStateEnum.INVITING.getCode() != chorusRecord.getState()) {
            throw new BsException(ErrorCode.CHORUS_RECORD_NOT_EXIST);
        }
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(chorusRecord.getOrderId());
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSongForKtv(orderSong.getStatus())) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }

        //更新合唱记录状态
        chorusRecord.setAssistantUuid(operator);
        if (deviceParam != null && !deviceParam.isEmpty()) {
            chorusRecord.setAssistantDeviceParam(GsonUtil.toJsonNoDouble(deviceParam));
        }
        chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_JOIN.getCode());

        // 设置歌曲状态为加入合唱
        singBaseInfo.setSongStatus(KtvSingStatusEnum.JOIN.getStatus());
        singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

        ChorusControlResultDto chorusControlResultDto = buildChorusResult(chorusRecord, orderSong, liveRecordDto);
        //判断串行还是实时合唱
        chorusControlResultDto.setChorusType(judgeChorusType(chorusRecord));

        //发送合唱同意消息
        ChorusBroadcastResultDto broadcastResultDto = new ChorusBroadcastResultDto(chorusControlResultDto, BasicUserDto.buildBasicUser(userService.getUser(operator)));
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(broadcastResultDto, EventTypeEnum.JOIN_CHORUS.getType()));

        //提交超时任务，当同意合唱后需要倒计时等待合唱准备完成（下载好伴奏、歌词、midi等信息
        JoinChorusTask task = new JoinChorusTask(chorusRecord);
        delayQueueProducer.send(DelayTopic.JOIN_CHORUS_TOPIC, GsonUtil.toJson(task), 15000);

        return chorusControlResultDto;
    }

    @Override
    public ChorusControlResultDto cancelChorus(String operator, CancelChorusParam param) {
        String roomUuid = param.getRoomUuid();

        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        ChorusRecord chorusRecord = chorusRecordService.getChorusRecordByChorusId(param.getChorusId());
        if (ChorusStateEnum.INVITING.getCode() != chorusRecord.getState()) {
            throw new BsException(ErrorCode.CHORUS_RECORD_NOT_EXIST);
        }
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null || !singBaseInfo.getChorusId().equals(param.getChorusId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "当前合唱歌曲信息不正确");
        }

        OrderSong orderSong = orderSongService.getAndCheckKtvOrderSong(operator, chorusRecord.getOrderId());

        chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_CANCEL.getCode());

        // 重置歌曲信息为未开始演唱
        singBaseInfo.setChorusId(null);
        singBaseInfo.setSongStatus(KtvSingStatusEnum.NOT_SING.getStatus());
        singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

        ChorusControlResultDto chorusControlResultDto = buildChorusResult(chorusRecord, orderSong, liveRecordDto);

        // 发送合唱取消消息
        ChorusBroadcastResultDto broadcastResultDto = new ChorusBroadcastResultDto(chorusControlResultDto, BasicUserDto.buildBasicUser(userService.getUser(operator)));
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(broadcastResultDto, EventTypeEnum.CANCEL_CHORUS.getType()));

        return chorusControlResultDto;
    }

    @Override
    public ChorusControlResultDto chorusReady(String operator, FinishChorusReadyParam param) {
        String roomUuid = param.getRoomUuid();
        LiveRecordDto liveRecordDto = liveRecordService.getLivingRecordByRoomUuid(roomUuid);
        SingBaseInfoDto singBaseInfo = singRedisWrapper.getSingBaseInfo(roomUuid);
        if (singBaseInfo == null || !singBaseInfo.getChorusId().equals(param.getChorusId())) {
            throw new BsException(ErrorCode.FORBIDDEN, "当前合唱信息不正确");
        }

        ChorusRecord chorusRecord = chorusRecordService.getChorusRecordByChorusId(param.getChorusId());

        if (ChorusStateEnum.CHORUS_JOIN.getCode() != chorusRecord.getState()) {
            throw new BsException(ErrorCode.CHORUS_NOT_AGREE);
        }
        if (!operator.equals(chorusRecord.getAssistantUuid())) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }

        //取消合唱同意超时任务
        delayQueueProducer.cancel(DelayTopic.JOIN_CHORUS_TOPIC, GsonUtil.toJson(new JoinChorusTask(chorusRecord)));

        chorusRecordService.updateChorusState(chorusRecord, ChorusStateEnum.CHORUS_READY.getCode());
        OrderSong orderSong = orderSongMapperWrapper.selectByPrimaryKey(chorusRecord.getOrderId());
        if (orderSong == null || !OrderSongStatusEnum.effectiveOrderSongForKtv(orderSong.getStatus())) {
            throw new BsException(ErrorCode.ORDER_SONG_NOT_EXISTS);
        }

        // 设置歌曲状态为加入合唱
        singBaseInfo.setSongStatus(KtvSingStatusEnum.READY.getStatus());
        singRedisWrapper.setSingBaseInfo(roomUuid, singBaseInfo);

        ChorusControlResultDto chorusControlResultDto = buildChorusResult(chorusRecord, orderSong, liveRecordDto);
        chorusControlResultDto.setChorusType(judgeChorusType(chorusRecord));

        // 发送合唱准备完成消息
        ChorusBroadcastResultDto broadcastResultDto = new ChorusBroadcastResultDto(chorusControlResultDto, BasicUserDto.buildBasicUser(userService.getUser(operator)));
        messageService.sendNeRoomChatMsg(roomUuid, new EventDto(broadcastResultDto, EventTypeEnum.PREPARE_CHORUS.getType()));
        return chorusControlResultDto;
    }



    private ChorusControlResultDto buildChorusResult(ChorusRecord chorusRecord, OrderSong orderSong, LiveRecordDto liveRecordDto) {
        ChorusControlResultDto chorusControlResultDto = new ChorusControlResultDto();
        BeanUtils.copyProperties(chorusRecord, chorusControlResultDto);

        chorusControlResultDto.setLiveTopic(liveRecordDto.getLiveTopic());
        chorusControlResultDto.setSingMode(liveRecordDto.getSingMode());
        chorusControlResultDto.setChorusStatus(chorusRecord.getState());

        //演唱者个人信息
        String leaderUuid = chorusRecord.getLeaderUuid();
        if (StringUtils.isNotEmpty(leaderUuid)) {
            chorusControlResultDto.setUserUuid(leaderUuid);
            UserDto userBasicInfo = userService.getUser(leaderUuid);
            if (userBasicInfo != null) {
                chorusControlResultDto.setUserName(userBasicInfo.getUserName());
                chorusControlResultDto.setIcon(userBasicInfo.getIcon());
            }
        }
        String assistantUuid = chorusRecord.getAssistantUuid();
        if (StringUtils.isNotEmpty(assistantUuid)) {
            chorusControlResultDto.setAssistantUuid(assistantUuid);
            UserDto userBasicInfo = userService.getUser(assistantUuid);
            if (userBasicInfo != null) {
                chorusControlResultDto.setAssistantName(userBasicInfo.getUserName());
                chorusControlResultDto.setAssistantIcon(userBasicInfo.getIcon());
            }
        }
        //歌曲信息
        chorusControlResultDto.setOrderSongInfo(orderSong);
        return chorusControlResultDto;
    }

    @Override
    public Integer judgeChorusType(ChorusRecord chorusRecord) {
        //默认串行合唱
        Integer chorusType = ChorusTypeEnum.SERIAL_CHORUS.getCode();
        //参数转换
        Map leaderDeviceMap = GsonUtil.fromJson(chorusRecord.getLeaderDeviceParam(), Map.class);
        Map assistantDeviceMap = GsonUtil.fromJson(chorusRecord.getAssistantDeviceParam(), Map.class);
        if (leaderDeviceMap == null || leaderDeviceMap.isEmpty() ||
                assistantDeviceMap == null || assistantDeviceMap.isEmpty()) {
            return chorusType;
        }
        //判断是否是有线耳机
        Double leaderWiredHeadset = (Double) leaderDeviceMap.get("wiredHeadset");
        Double assistantWiredHeadset = (Double) assistantDeviceMap.get("wiredHeadset");
        if (leaderWiredHeadset == null || leaderWiredHeadset != 1 ||
                assistantWiredHeadset == null || assistantWiredHeadset != 1) {
            return chorusType;
        }
        //必要参数校验
        Double leaderPlayDelay = (Double) leaderDeviceMap.get("playDelay");
        Double assistantPlayDelay = (Double) assistantDeviceMap.get("playDelay");
        Double leaderRtt = (Double) leaderDeviceMap.get("rtt");
        Double assistantRtt = (Double) assistantDeviceMap.get("rtt");
        if (leaderPlayDelay == null || assistantPlayDelay == null || leaderRtt == null || assistantRtt == null) {
            return chorusType;
        }
        //校验是否能实时合唱
        Double chorusThreshold = 500d;
        if ((leaderPlayDelay + leaderRtt + assistantRtt) < chorusThreshold &&
                (assistantPlayDelay + leaderRtt + assistantRtt) < chorusThreshold) {
            chorusType = ChorusTypeEnum.REALTIME_CHORUS.getCode();
        }
        return chorusType;
    }
}
