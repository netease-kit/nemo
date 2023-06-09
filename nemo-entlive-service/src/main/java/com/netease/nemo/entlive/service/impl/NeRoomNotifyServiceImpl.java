package com.netease.nemo.entlive.service.impl;

import com.google.gson.JsonObject;
import com.netease.nemo.entlive.parameter.neroomNotify.CloseRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.CreateRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.JoinRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.LeaveRoomEventNotify;
import com.netease.nemo.entlive.service.EntNotifyService;
import com.netease.nemo.enums.NeRoomNotifyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.service.NotifyService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("neRoomNotifyServiceImpl")
@Slf4j
public class NeRoomNotifyServiceImpl implements NotifyService {

    @Resource
    private EntNotifyService entNotifyService;

    @Override
    public void handlerNotify(String body) {
        handleNeRoomMsg(body);
    }

    /**
     * TODO 注：仅为云信派对——语聊房处理逻辑
     */
    public void handleNeRoomMsg(String body) {
        log.info("handleNeRoomNotifyMsg body: {}", body);
        try {
            JsonObject jsonObject = GsonUtil.parseJsonObject(body);
            String eventType = GsonUtil.getString(jsonObject, "eventType");
            NeRoomNotifyEnum eventTypeEnum = NeRoomNotifyEnum.fromCode(eventType);
            if (eventTypeEnum == null) {
                log.info("handleNeRoomNotifyMsg eventType:{} not support", eventType);
                return;
            }
            JsonObject data = GsonUtil.getJsonObject(jsonObject, "body");
            switch (eventTypeEnum) {
                case CREATE_ROOM: {
                    CreateRoomEventNotify param = GsonUtil.fromJson(data, CreateRoomEventNotify.class);
                    entNotifyService.handlerCreateRoom(param);
                    break;
                }
                case CLOSE_ROOM: {
                    CloseRoomEventNotify param = GsonUtil.fromJson(data, CloseRoomEventNotify.class);
                    entNotifyService.handlerCloseRoom(param);
                    break;
                }
                case USER_JOIN_ROOM: {
                    JoinRoomEventNotify param = GsonUtil.fromJson(data, JoinRoomEventNotify.class);
                    entNotifyService.handlerUserJoinRoom(param);
                    break;
                }
                case USER_LEAVE_ROOM: {
                    LeaveRoomEventNotify param = GsonUtil.fromJson(data, LeaveRoomEventNotify.class);
                    entNotifyService.handlerUserLeaveRoom(param);
                    break;
                }
                default:
                    break;
            }
        } catch (BsException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
