package com.netease.nemo.entlive.service;


import com.netease.nemo.entlive.dto.OrderSongDto;
import com.netease.nemo.entlive.dto.OrderSongResultDto;
import com.netease.nemo.entlive.model.po.OrderSong;

import java.util.List;

/**
 * 娱乐直播房间——点歌台接口
 *
 * @Author：CH
 * @Date：2023/5/17
 */
public interface OrderSongService {


    int addOrderSong(OrderSong orderSong);


    /**
     * 点歌
     *
     * @param orderSongDto 请求点歌信息信息
     */
    OrderSongResultDto orderSong(OrderSongDto orderSongDto);

    /**
     * 歌曲置顶
     *
     * @param liveRecordId 直播编号
     * @param operator    操作者（userUUid）
     * @param orderId     点歌编号
     */
    void songSetTop(Long liveRecordId, String operator, Long orderId);


    /**
     * 删除点歌
     *
     * @param liveRecordId 直播编号
     * @param operator 操作者（userUUid）
     * @param orderId 点歌编号
     */
    void cancelOrderSong(Long liveRecordId, String operator, Long orderId);

    /**
     * 切歌
     *
     * @param liveRecordId 直播编号
     * @param operator    操作者（userUUid）
     * @param orderId    点歌唯一编号
     * @param attachment 请求头信息
     */
    void orderSongSwitch(Long liveRecordId, String operator,  Long orderId, String attachment);


    /**
     * 获取歌曲列表
     *
     * @param liveRecordId 直播间编号
     * @return 返回点歌列表
     */
    List<OrderSongResultDto> getOrderSongs(Long liveRecordId);


    /**
     * 获取用户K歌房内已点歌曲列表
     *
     * @param liveRecordId 直播间唯一编号
     * @param userUuid 用户编号
     * @return 返回点歌列表
     */
    List<OrderSongResultDto> getUserOrderSongs(Long liveRecordId, String userUuid);

    /**
     * 清空房间内点歌数据
     *
     * @param liveRecordId 直播间唯一编号
     */
    void cleanOrderSongs(Long liveRecordId);

    /**
     * 清空用户点歌数据
     *
     * @param liveRecordId 直播间唯一编号
     * @param userUuid 观众
     */
    void cleanUserOrderSongs(Long liveRecordId, String userUuid);
}
