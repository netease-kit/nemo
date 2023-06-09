package com.netease.nemo.entlive.service;

import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.paramters.neroom.NeRoomMessageParam;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 娱乐直播房间——NeRoom消息服务
 *
 * @Author：CH
 * @Date：2023/5/17
 */
@Service
@Slf4j
public class MessageService {

    @Resource
    private NeRoomService neRoomService;

    /**
     * 发送聊天室协议
     *
     * @param roomUuid 直播间关联NeRoom房间编号
     * @param message 消息体
     */
    public void sendNeRoomChatMsg(String roomUuid, Object message) {
        neRoomService.sendNeRoomCustomMessage(NeRoomMessageParam.builder().roomUuid(roomUuid).message(GsonUtil.toJson(message)).build());
    }
}
