package com.netease.nemo.entlive.wrapper;

import com.netease.nemo.entlive.dto.LiveRewardTotalDto;
import com.netease.nemo.entlive.mapper.LiveRewardMapper;
import com.netease.nemo.entlive.model.po.LiveReward;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@CacheConfig(cacheNames = "Live_Reward")
public class LiveRewardMapperWrapper extends BaseWrapper {

    @Resource
    private LiveRewardMapper liveRewardMapper;

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.liveRecordId != null", key = "'countRewardTotal_liveRecordId_'.concat(#record.liveRecordId)"),
            @CacheEvict(condition = "#record.liveRecordId != null &&  #record.target != null", key = "'countUserRewardTotal_liveRecordId_'.concat(#record.liveRecordId).concat('target_').concat(#record.target)"),
    })
    public int insert(LiveReward record) {
        return liveRewardMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.liveRecordId != null", key = "'countRewardTotal_liveRecordId_'.concat(#record.liveRecordId)"),
            @CacheEvict(condition = "#record.liveRecordId != null &&  #record.target != null", key = "'countUserRewardTotal_liveRecordId_'.concat(#record.liveRecordId).concat('target_').concat(#record.target)"),
    })
    public int insertSelective(LiveReward record) {
        return liveRewardMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public LiveReward selectByPrimaryKey(Long id) {
        return liveRewardMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.liveRecordId != null", key = "'countRewardTotal_liveRecordId_'.concat(#record.liveRecordId)"),
            @CacheEvict(condition = "#record.liveRecordId != null &&  #record.target != null", key = "'countUserRewardTotal_liveRecordId_'.concat(#record.liveRecordId).concat('target_').concat(#record.target)"),
    })
    public int updateByPrimaryKeySelective(LiveReward record) {
        return liveRewardMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.liveRecordId != null", key = "'countRewardTotal_liveRecordId_'.concat(#record.liveRecordId)"),
            @CacheEvict(condition = "#record.liveRecordId != null &&  #record.target != null", key = "'countUserRewardTotal_liveRecordId_'.concat(#record.liveRecordId).concat('target_').concat(#record.target)"),
    })
    public int updateByPrimaryKey(LiveReward record) {
        return liveRewardMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'countRewardTotal_liveRecordId_'.concat(#liveRecordId)", unless = "#result == null")
    public LiveRewardTotalDto countRewardTotal(Long liveRecordId) {
        return liveRewardMapper.countRewardTotal(liveRecordId);
    }

    @Cacheable(key = "'countUserRewardTotal_liveRecordId_'.concat(#liveRecordId).concat('target_').concat(#target)", unless = "#result == null")
    public LiveRewardTotalDto countUserRewardTotal(Long liveRecordId, String target) {
        return liveRewardMapper.countUserRewardTotal(liveRecordId, target);
    }


    public int batchInsert(List<LiveReward> liveRewards) {
        int res = liveRewardMapper.batchInsert(liveRewards);

        // 批量删除key
        nemoRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (LiveReward liveReward : liveRewards) {
                String orderSongCountKey = RedisUtil.springCacheJoinKey("Live_Reward", "countRewardTotal_liveRecordId_" + liveReward.getLiveRecordId());
                String OrderSongUserCountKey = RedisUtil.springCacheJoinKey("Live_Reward", "countUserRewardTotal_liveRecordId_" + liveReward.getLiveRecordId() + "target_" + liveReward.getTarget());
                String idKey = RedisUtil.springCacheJoinKey("Live_Reward", "id_" + liveReward.getId());

                deleteKey(connection, orderSongCountKey);
                deleteKey(connection, OrderSongUserCountKey);
                deleteKey(connection, idKey);
            }
            return null;
        });
        return res;
    }

}