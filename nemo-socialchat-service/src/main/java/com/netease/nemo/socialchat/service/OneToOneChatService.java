package com.netease.nemo.socialchat.service;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.socialchat.dto.UserRewardDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomUserNotifyParam;

import java.util.List;

public interface OneToOneChatService {

    /**
     * 用户上报，仅做的demo演示逻辑
     *
     * @param userUuid 用户唯一编号
     * @param deviceId 用户设备编号
     */
    void reporter(String appKey, String userUuid, String deviceId);

    /**
     * 查询1v1在线用户列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return 返回在线用户列表信息
     */
    List<OnLineUserDto> getOnLineUser(String appKey, Integer pageNum, Integer pageSize);

    /**
     * 根据手机号判断用户在线状态
     *
     * @param mobile 手机号
     * @return online-在线， offline-下线
     */
    String getUserState(String appKey, String mobile);

    /**
     * 根据userUuid获取用户信息
     *
     * @param userUuid 用户唯一编号
     * @return UserDto
     */
    UserDto getUserInfo(String appKey, String userUuid);

    /**
     * 根据userUuid和deviceId获取用户信息
     *
     * @param userUuid userUuid
     * @param deviceId deviceId
     * @return UserDto
     */
    UserDto getUserInfo(String appKey, String userUuid, String deviceId);

    /**
     * 保存RTC开始结束信息
     *
     * @param param RTC房间抄送信息
     */
    void saveRtcRecord(String appKey, RtcRoomNotifyParam param);

    /**
     * 保存用户进出RTC房间抄送信息
     *
     * @param param 用户进出rtc房间信息
     */
    void saveRtcUserRecord(String appKey, RtcRoomUserNotifyParam param);

    /**
     * 根据音视频cid获取rtc房间中成员信息
     *
     * @param channelId 音视频cid
     * @return rtc成员信息列表
     */
    List<RtcRoomUserInfoDto> getRtcRoomUsersByChannelId(String appKey, Long channelId);

    /**
     * 根据音视频cid查询音视频房间信息
     *
     * @param channelId 音视频cid
     * @return 返回rtc房间信息
     */
    RtcRoomInfoDto getRtcRoomInfoDtoByChannelId(String appKey, Long channelId);

    /**
     * 用户打赏
     *
     * @param userRewardDto 打赏对象
     */
    void userReward(UserRewardDto userRewardDto);

    /**
     * 注： 云信派对1v1娱乐demo虚拟账号初始化
     *
     * @return 返回测试账号
     */
    List<UserDto> initOneOneVirtuallyUser();

}
