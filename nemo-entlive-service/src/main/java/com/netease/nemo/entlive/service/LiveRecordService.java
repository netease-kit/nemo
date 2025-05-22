package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.openApi.dto.neroom.UserOnSeatNotifyDto;

import java.util.List;

/**
 * 娱乐直播房间——直播记录接口
 *
 * @Author：CH
 * @Date：2023/5/16
 */
public interface LiveRecordService {
    /**
     * 添加直播记录
     *
     * @param liveRecord liveRecord
     * @return 直播编号
     */
    Long addLiveRecord(LiveRecord liveRecord);

    /**
     * 查询开播记录
     *
     * @param liveRecordId 直播编号
     * @return LiveRecordDto
     */
    LiveRecordDto getLiveRecord(Long liveRecordId);

    /**
     * 获取主播当前直播的直播记录 不存在则报错
     *
     * @param userUuid 主播账号
     * @return LiveRecordDto
     */
    LiveRecordDto getLivingRecordByUserUuid(String userUuid);

    /**
     * 获取主播当前直播的直播记录 不存在则报错
     *
     * @param roomArchiveId 虚拟房间唯一编号
     * @return LiveRecordDto
     */
    LiveRecordDto getLivingRecordByRoomArchiveId(String roomArchiveId);


    /**
     * 获取主播当前直播的直播记录 不存在则报错
     *
     * @param roomUuid 房间uuid唯一编号
     * @return LiveRecordDto
     */
    LiveRecordDto getLivingRecordByRoomUuid(String roomUuid);


    /**
     * 获取主播当前直播的直播记录 不存在则返回null
     *
     * @param userUuid 主播账号
     * @return LiveRecordDto
     */
    LiveRecordDto getLiveRecordByUserUuid(String userUuid);


    /**
     * 校验主播是否正在直播
     *
     * @param userUuid 主播账号
     * @return true or false
     */
    boolean checkAnchorLiving(String userUuid);

    /**
     * 更新直播状态
     *
     * @param liveRecordId 直播编号
     * @param live         直播状态
     */
    void updateLiveState(Long liveRecordId, Integer live);


    /**
     * 系统回收未开始的直播
     *
     * @param liveRecordId 直播记录编号
     */
    void systemRecycleNotLiveRecord(Long liveRecordId);

    /**
     * 更新直播状态
     *
     * @param liveRecord 直播记录
     * @return int
     */
    void updateLiveRecord(LiveRecord liveRecord);

    /**
     * 标记直播记录失效（关播）
     *
     * @param liveRecordId 直播记录编号
     * @return int
     */
    int invalidLiveRecord(Long liveRecordId);

    /**
     * 根据直播类型查询直播记录
     *
     * @param liveType        房间类型
     * @param excludeUserUuid 过滤账号
     * @return List<LiveRecordDto>
     */
    List<LiveRecordDto> getLivingRecords(Integer liveType, String excludeUserUuid);


    /**
     * 根据roomArchiveId获取直播房间记录
     *
     * @param roomArchiveId neRoom房间唯一编号
     * @return LiveRecordDto
     */
    LiveRecordDto getLiveRecordByRoomArchiveId(String roomArchiveId);

    List<UserOnSeatNotifyDto.SeatUser> getAllSeatUsersTyped(String roomArchiveId, String roomManager);

    LiveRecordDto getLiveRecord( String userUuid, List<Integer> ongoingState);

}
