package com.netease.nemo.entlive.service;

import com.github.pagehelper.PageInfo;
import com.netease.nemo.entlive.dto.LiveDefaultInfoDto;
import com.netease.nemo.entlive.dto.LiveIntroDto;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;

/**
 * 娱乐直播间接口{语聊房、、一起听、pk直播、连麦等}
 *
 * @Author：CH
 * @Date：2023/5/18 10:57 下午
 */
public interface EntLiveService {

    /**
     * 获取娱乐多人房间默认信息：主题、背景图等
     *
     * @return LiveDefaultInfoDto  房间默认信息
     */
    LiveDefaultInfoDto getDefaultLiveInfo();

    /**
     * 主播开播(语聊房/多人连麦/pk直播/ktv/一起听等)
     *
     * @param param 主播开播房间信息
     * @return LiveIntroDto 直播房间信息
     */
    LiveIntroDto createLiveRoom(CreateLiveParam param);

    /**
     * 获取娱乐直播房间详细信息
     *
     * @param liveRecordId liveRecordId
     * @return LiveIntroDto
     */
    LiveIntroDto getLiveInfo(Long liveRecordId);

    /**
     * 结束娱乐房间
     *
     * @param operator     操作者（userUuid）
     * @param liveRecordId 直播唯一记录编号
     */
    void closeLiveRoom(String operator, Long liveRecordId);

    void entryLiveRoom(String userUuid, Long liveRecordId);


    /**
     * 分页查询直播记录
     *
     * @param param 直播列表查询对象
     * @return PageInfo<LiveIntroDto>
     */
    PageInfo<LiveIntroDto> getLiveRoomList(LiveListQueryParam param);

    /**
     * 用户打赏
     *
     * @param liveRewardParam liveRewardParam对象
     */
    void liveReward(LiveRewardParam liveRewardParam);
}
