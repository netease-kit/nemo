package com.netease.nemo.wrapper;

import com.netease.nemo.mapper.UserMapper;
import com.netease.nemo.model.po.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@CacheConfig(cacheNames = "User")
public class UserMapperWrapper {

    @Resource
    private UserMapper userMapper;

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(key = "'mobile_'.concat(#record.mobile)"),
    })
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(key = "'mobile_'.concat(#record.mobile)"),
    })
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(key = "'mobile_'.concat(#record.mobile)"),
    })
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(key = "'mobile_'.concat(#record.mobile)"),
    })
    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'userUuid_' + #userUuid", unless = "#result == null")
    public User selectByUserUuid(String userUuid) {
        return userMapper.selectByUserUuid(userUuid);
    }

    @Cacheable(key = "'mobile_' + #mobile", unless = "#result == null")
    public User getUserByMobile(String mobile) {
        return userMapper.getUserByMobile(mobile);
    }
}