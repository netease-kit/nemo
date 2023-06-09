package com.netease.nemo.wrapper;

import com.netease.nemo.mapper.GiftMapper;
import com.netease.nemo.model.po.Gift;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 礼物数据缓存类
 *
 * @Author：CH
 * @Date：2023/6/2 11:29 上午
 */
@Component
@Slf4j
@CacheConfig(cacheNames = "Gift")
public class GiftMapperWrapper {

    @Resource
    private GiftMapper giftMapper;

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#id)"),
    })
    public int deleteByPrimaryKey(Long id) {
        return giftMapper.deleteByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
    })
    public int insert(Gift record) {
        return giftMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
    })
    public int insertSelective(Gift record) {
        return giftMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public Gift selectByPrimaryKey(Long id) {
        return giftMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
    })
    public int updateByPrimaryKeySelective(Gift record) {
        return giftMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
    })
    public int updateByPrimaryKey(Gift record) {
        return giftMapper.updateByPrimaryKey(record);
    }
}