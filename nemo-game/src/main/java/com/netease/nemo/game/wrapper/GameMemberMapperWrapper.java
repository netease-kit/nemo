package com.netease.nemo.game.wrapper;

import com.netease.nemo.game.mapper.GameMemberHistoryMapper;
import com.netease.nemo.game.mapper.GameMemberMapper;
import com.netease.nemo.game.mapper.GameRecordMapper;
import com.netease.nemo.game.model.po.GameMember;
import com.netease.nemo.game.model.po.GameMemberHistory;
import com.netease.nemo.game.model.po.GameRecord;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@CacheConfig(cacheNames = "GameMember")
public class GameMemberMapperWrapper {

    @Resource
    private GameMemberMapper gameMemberMapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private GameMemberHistoryMapper gameMemberHistoryMapper;

    @Caching(evict = {
            @CacheEvict(key = "'GameMemberList'.concat('_roomUuid_') + #record.roomUuid.concat('_gameId_') + #record.gameId"),
            @CacheEvict(key = "'GameMemberList_'.concat('gameRecordId_').concat(#record.gameRecordId)"),
    })
    public int insertSelective(GameMember record) {
        return gameMemberMapper.insertSelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'GameMemberList'.concat('_roomUuid_') + #record.roomUuid.concat('_gameId_') + #record.gameId"),
            @CacheEvict(key = "'GameMemberList_'.concat('gameRecordId_').concat(#record.gameRecordId)"),
    })
    public int updateByPrimaryKey(GameMember record) {
        return gameMemberMapper.updateByPrimaryKey(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'GameMemberList'.concat('_roomUuid_') + #record.roomUuid.concat('_gameId_') + #record.gameId"),
            @CacheEvict(key = "'GameMemberList_'.concat('gameRecordId_').concat(#record.id)"),
    })
    public int deleteGameMember(GameRecord record) {
        List<GameMember> gameMembers = selectByGameRecordId(record.getId());
        if(CollectionUtils.isEmpty(gameMembers)) {
            return 0;
        }

        List<GameMemberHistory> gameMemberHistoryList = gameMembers.stream().map(o -> modelMapper.map(o, GameMemberHistory.class)).collect(Collectors.toList());
        gameMemberHistoryMapper.insertList(gameMemberHistoryList);

        return gameMemberMapper.deleteGameMemberById(record.getId());
    }


    @Cacheable(key = "'GameMemberList'.concat('_roomUuid_') + #roomUuid.concat('_gameId_') + #gameId", unless = "#result == null")
    public List<GameMember> selectByRoomUuid(String roomUuid, String gameId) {
        return gameMemberMapper.selectGameMembers(roomUuid, gameId);
    }

    @Cacheable(key = "'GameMemberList_'.concat('gameRecordId_') + #gameRecordId", unless = "#result == null")
    public List<GameMember> selectByGameRecordId(Long gameRecordId) {
        return gameMemberMapper.selectGameMembersById(gameRecordId);
    }
}