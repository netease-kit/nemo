package com.netease.nemo.wrapper;

import com.netease.nemo.mapper.UserRewardMapper;
import com.netease.nemo.model.po.UserReward;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 1v1打赏数据缓存类
 *
 * @Author：CH
 * @Date：2023/6/2 11:29 上午
 */
@Component
@Slf4j
@CacheConfig(cacheNames = "UserReward")
public class UserRewardMapperWrapper {

    @Resource
    private UserRewardMapper userRewardMapper;

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)")
    })
    public int insert(UserReward record) {
        return userRewardMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)")
    })
    public int insertSelective(UserReward record) {
        return userRewardMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public UserReward selectByPrimaryKey(Long id) {
        return userRewardMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)")
    })
    public int updateByPrimaryKeySelective(UserReward record){
        return userRewardMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)")
    })
    public int updateByPrimaryKey(UserReward record) {
        return userRewardMapper.updateByPrimaryKey(record);
    }
}