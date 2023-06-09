package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.parameter.neroomNotify.CloseRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.CreateRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.JoinRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.LeaveRoomEventNotify;

/**
 * 娱乐直播房间——neRoom抄送处理
 *
 * @Author：CH
 * @Date：2023/5/17
 */
public interface EntNotifyService {

    void handlerCreateRoom(CreateRoomEventNotify param);

    void handlerCloseRoom(CloseRoomEventNotify param);

    void handlerUserJoinRoom(JoinRoomEventNotify param);

    void handlerUserLeaveRoom(LeaveRoomEventNotify param);
}
