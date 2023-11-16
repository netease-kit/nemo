package com.netease.nemo.entlive.wrapper;

import com.netease.nemo.entlive.mapper.LiveRecordMapper;
import com.netease.nemo.entlive.model.po.LiveRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 直播记录缓存类
 *
 * @Author：CH
 * @Date：2023/6/1 1:49 下午
 */
@Component
@Slf4j
@CacheConfig(cacheNames = "Live_Record")
public class LiveRecordWrapper {

    @Resource
    private LiveRecordMapper liveRecordMapper;


    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.userUuid != null", key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(condition = "#record.roomArchiveId != null", key = "'roomArchiveId_'.concat(#record.roomArchiveId)"),
            @CacheEvict(condition = "#record.roomUuid != null", key = "'roomUuid_'.concat(#record.roomUuid)"),
            @CacheEvict(condition = "#record.userUuid != null &&  #record.liveType != null", key = "'userUuid_'.concat(#record.userUuid).concat('liveType_').concat(#record.liveType)"),
    })
    public int insert(LiveRecord record) {
        return liveRecordMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.userUuid != null", key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(condition = "#record.roomArchiveId != null", key = "'roomArchiveId_'.concat(#record.roomArchiveId)"),
            @CacheEvict(condition = "#record.roomUuid != null", key = "'roomUuid_'.concat(#record.roomUuid)"),
            @CacheEvict(condition = "#record.userUuid != null &&  #record.liveType != null", key = "'userUuid_'.concat(#record.userUuid).concat('liveType_').concat(#record.liveType)"),
    })
    public int insertSelective(LiveRecord record) {
        return liveRecordMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public LiveRecord selectByPrimaryKey(Long id) {
        return liveRecordMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.userUuid != null", key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(condition = "#record.roomArchiveId != null", key = "'roomArchiveId_'.concat(#record.roomArchiveId)"),
            @CacheEvict(condition = "#record.roomUuid != null", key = "'roomUuid_'.concat(#record.roomUuid)"),
            @CacheEvict(condition = "#record.userUuid != null &&  #record.liveType != null", key = "'userUuid_'.concat(#record.userUuid).concat('liveType_').concat(#record.liveType)"),
    })
    public int updateByPrimaryKeySelective(LiveRecord record) {
        return liveRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(condition = "#record.userUuid != null", key = "'userUuid_'.concat(#record.userUuid)"),
            @CacheEvict(condition = "#record.roomArchiveId != null", key = "'roomArchiveId_'.concat(#record.roomArchiveId)"),
            @CacheEvict(condition = "#record.roomUuid != null", key = "'roomUuid_'.concat(#record.roomUuid)"),
            @CacheEvict(condition = "#record.userUuid != null &&  #record.liveType != null", key = "'userUuid_'.concat(#record.userUuid).concat('liveType_').concat(#record.liveType)"),
    })
    public int updateByPrimaryKey(LiveRecord record) {
        return liveRecordMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'userUuid_'.concat(#userUuid).concat('liveType_').concat(#liveType)", unless = "#result == null")
    public LiveRecord selectByUserUuidAndType(String userUuid, Integer liveType) {
        return liveRecordMapper.selectByUserUuidAndType(userUuid, liveType);
    }

    @Cacheable(key = "'roomArchiveId_' + #roomArchiveId", unless = "#result == null")
    public LiveRecord selectByRoomArchiveId(String roomArchiveId) {
        return liveRecordMapper.selectByRoomArchiveId(roomArchiveId);
    }

    @Cacheable(key = "'userUuid_' + #userUuid", unless = "#result == null")
    public LiveRecord selectByUserUuid(String userUuid) {
        return liveRecordMapper.selectByUserUuid(userUuid);
    }

    @Cacheable(key = "'roomUuid_' + #roomUuid", unless = "#result == null")
    public LiveRecord selectByRoomUuid(String roomUuid) {
        return liveRecordMapper.selectByRoomUuid(roomUuid);
    }
}
