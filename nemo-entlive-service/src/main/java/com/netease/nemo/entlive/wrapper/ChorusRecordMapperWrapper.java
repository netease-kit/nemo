package com.netease.nemo.entlive.wrapper;


import com.netease.nemo.entlive.mapper.ChorusRecordMapper;
import com.netease.nemo.entlive.model.po.ChorusRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@CacheConfig(cacheNames = "ChorusRecord_")
public class ChorusRecordMapperWrapper {

    @Resource
    private ChorusRecordMapper chorusRecordMapper;


    @Caching(evict = {
            @CacheEvict(key = "'chorusRecord_'.concat(#record.id)"),
            @CacheEvict(key = "'chorusRecord_'.concat('_chorusId_').concat(#record.chorusId)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid).concat('_orderId_').concat(#record.orderId)")
    })
    public int insert(ChorusRecord record) {
        return chorusRecordMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'chorusRecord_'.concat(#record.id)"),
            @CacheEvict(key = "'chorusRecord_'.concat('_chorusId_').concat(#record.chorusId)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid).concat('_orderId_').concat(#record.orderId)")
    })
    public int insertSelective(ChorusRecord record) {
        return chorusRecordMapper.insertSelective(record);
    }


    @Caching(evict = {
            @CacheEvict(key = "'chorusRecord_'.concat(#record.id)"),
            @CacheEvict(key = "'chorusRecord_'.concat('_chorusId_').concat(#record.chorusId)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid).concat('_orderId_').concat(#record.orderId)")
    })
    public int updateByPrimaryKeySelective(ChorusRecord record) {
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(new Date());
        }
        return chorusRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'chorusRecord_'.concat(#record.id)"),
            @CacheEvict(key = "'chorusRecord_'.concat('_chorusId_').concat(#record.chorusId)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid)"),
            @CacheEvict(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#record.roomUuid).concat('_orderId_').concat(#record.orderId)")
    })
    public int updateByPrimaryKey(ChorusRecord record) {
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(new Date());
        }
        return chorusRecordMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'chorusRecord_'.concat(#id)", unless = "#result == null")
    public ChorusRecord selectByPrimaryKey(Long id) {
        return chorusRecordMapper.selectByPrimaryKey(id);
    }

    @Cacheable(key = "'chorusRecord_'.concat('_chorusId_').concat(#chorusId)", unless = "#result == null")
    public ChorusRecord selectByChorusId(@Param("chorusId") String chorusId) {
        return chorusRecordMapper.selectByChorusId(chorusId);
    }

    @Cacheable(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#roomUuid)", unless = "#result == null")
    public List<ChorusRecord> selectNotEndChorusRecord(@Param("roomUuid") String roomUuid) {
        return chorusRecordMapper.selectNotEndChorusRecord(roomUuid);
    }

    @Cacheable(key = "'chorusRecordList_'.concat('_roomUuid_').concat(#roomUuid).concat('_orderId_').concat(#orderId)", unless = "#result == null")
    public List<ChorusRecord> selectOrderSongChorusRecord(@Param("roomUuid") String roomUuid,
                                                          @Param("orderId") Long orderId) {
        return chorusRecordMapper.selectOrderSongChorusRecord(roomUuid, orderId);
    }
}