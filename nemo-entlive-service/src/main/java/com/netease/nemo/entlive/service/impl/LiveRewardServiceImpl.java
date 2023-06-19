package com.netease.nemo.entlive.service.impl;

import com.netease.nemo.entlive.dto.LiveRecordDto;
import com.netease.nemo.entlive.dto.LiveRewardTotalDto;
import com.netease.nemo.entlive.mapper.LiveRewardMapper;
import com.netease.nemo.entlive.model.po.LiveReward;
import com.netease.nemo.entlive.service.LiveRewardService;
import com.netease.nemo.entlive.wrapper.LiveRewardMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 直播间打赏服务
 *
 * @Author：CH
 * @Date：2023/5/19 5:53 下午
 */
@Service
@Slf4j
public class LiveRewardServiceImpl implements LiveRewardService {

    @Resource
    private LiveRewardMapperWrapper liveRewardMapperWrapper;

    @Override
    public void batchReward(String rewarder, List<String> targets, LiveRecordDto liveRecordDto, Long giftId, Integer giftCount) {
        // TODO
    }

    @Override
    public Long getLiveRewordTotal(Long liveRecordId) {
        LiveRewardTotalDto liveRewardTotalDto = liveRewardMapperWrapper.countRewardTotal(liveRecordId);
        if (liveRewardTotalDto != null) {
            return liveRewardTotalDto.getRewardTotal();
        }
        return 0L;
    }

    @Override
    public LiveRewardTotalDto countUserRewardTotal(Long liveRecordId, String target) {
        return liveRewardMapperWrapper.countUserRewardTotal(liveRecordId,target);
    }

    @Override
    public void batchInsertLiveReward(List<LiveReward> liveRewards) {
        if(CollectionUtils.isEmpty(liveRewards))  {
            return;
        }
        liveRewardMapperWrapper.batchInsert(liveRewards);
    }
}
