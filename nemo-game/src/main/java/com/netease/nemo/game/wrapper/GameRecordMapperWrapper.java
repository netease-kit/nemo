package com.netease.nemo.game.wrapper;

import com.netease.nemo.game.mapper.GameRecordHistoryMapper;
import com.netease.nemo.game.mapper.GameRecordMapper;
import com.netease.nemo.game.model.po.GameMemberHistory;
import com.netease.nemo.game.model.po.GameRecord;
import com.netease.nemo.game.model.po.GameRecordHistory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.stream.Collectors;

@Component
@Slf4j
@CacheConfig(cacheNames = "GameRecord")
public class GameRecordMapperWrapper {

    @Resource
    private GameRecordMapper gameRecordMapper;

    @Resource
    private GameRecordHistoryMapper gameRecordHistoryMapper;

    @Resource
    private ModelMapper modelMapper;

    @Caching(evict = {
            @CacheEvict(key = "'_roomUuid_' + #record.roomUuid"),
            @CacheEvict(condition = "#record.id != null", key = "'id_'.concat(#record.id)")
    })
    public int insert(GameRecord record) {
        return gameRecordMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'_roomUuid_' + #record.roomUuid"),
            @CacheEvict(condition = "#record.id != null", key = "'id_'.concat(#record.id)")
    })
    public int insertSelective(GameRecord record) {
        return gameRecordMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public GameRecord selectByPrimaryKey(Long id) {
        return gameRecordMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'_roomUuid_' + #record.roomUuid"),
            @CacheEvict(condition = "#record.id != null", key = "'id_'.concat(#record.id)")
    })
    public int updateByPrimaryKeySelective(GameRecord record) {
        return gameRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'_roomUuid_' + #record.roomUuid"),
            @CacheEvict(condition = "#record.id != null", key = "'id_'.concat(#record.id)")
    })
    public int updateByPrimaryKey(GameRecord record) {
        return gameRecordMapper.updateByPrimaryKey(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'_roomUuid_' + #record.roomUuid"),
            @CacheEvict(condition = "#record.id != null", key = "'id_'.concat(#record.id)")
    })
    public int deleteGameRecord(GameRecord record) {
        GameRecordHistory gameRecordHistory = modelMapper.map(record, GameRecordHistory.class);
        gameRecordHistory.setGameRecordId(record.getId());
        gameRecordHistoryMapper.insertSelective(gameRecordHistory);
        return gameRecordMapper.deleteByPrimaryKey(record.getId());
    }

    @Cacheable(key = "'_roomUuid_' + #roomUuid", unless = "#result == null")
    public GameRecord selectByRoomUuid(String roomUuid) {
        return gameRecordMapper.selectByRoomUuid(roomUuid);
    }
}