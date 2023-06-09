package com.netease.nemo.entlive.service;


import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.dto.LiveRewardTotalDto;
import com.netease.nemo.entlive.model.po.LiveReward;

import java.util.List;

/**
 * 娱乐直播房间——直播打赏相关接口
 *
 * @Author：CH
 * @Date：2023/5/17
 */
public interface LiveRewardService {

    /**
     * 批量打赏
     *
     * @param rewarder      打赏者
     * @param targets       被打赏者List
     * @param liveRecordDto 直播信息
     * @param giftId        礼物编号
     * @param giftCount     打赏数量
     */
    void batchReward(String rewarder, List<String> targets, LiveRecordDto liveRecordDto, Long giftId, Integer giftCount);


    /**
     * 获取直播间打赏总数
     *
     * @param liveRecordId 直播记录
     * @return 直播打赏总额
     */
    Long getLiveRewordTotal(Long liveRecordId);


    /**
     * 获取直播间打赏总数
     *
     * @param liveRecordId 直播间唯一记录
     * @param target       被打赏者
     * @return LiveRewardTotalDto
     */
    LiveRewardTotalDto countUserRewardTotal(Long liveRecordId, String target);


    /**
     * 批量添加打赏记录
     *
     * @param liveRewards 直播打赏对象列表
     * @return true-成功 false-失败
     */
    void batchInsertLiveReward(List<LiveReward> liveRewards);
}
