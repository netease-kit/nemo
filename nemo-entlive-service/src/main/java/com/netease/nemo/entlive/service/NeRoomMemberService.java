package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;

import java.util.List;

/**
 * 娱乐直播房间——NeRoom成员处理接口
 *
 * @Author：CH
 * @Date：2023/5/17
 */
public interface NeRoomMemberService {

    /**
     * 获取NeRoom成员信息
     *
     * @param roomArchiveId NeRoom房间唯一编号
     * @return List<RoomMember>
     */
    List<RoomMember> getRoomMembers(String roomArchiveId);


    /**
     * 获取NeRoom成员总数
     *
     * @param roomArchiveId NeRoom房间唯一编号
     * @return 成员数
     */
    long getRoomMemberSize(String roomArchiveId);

    /**
     * 删除用户
     *
     * @param roomMembers roomMembers
     */
    void deleteRoomMembers(String roomArchiveId, List<RoomMember> roomMembers);

    /**
     * 判断用户是否在NeRoom中
     * @param roomArchiveId neRoom唯一编号
     * @param userUuid 用户 userUuid编号
     * @return ture of false
     */
    boolean userInNeRoom(String roomArchiveId,String userUuid);
}
