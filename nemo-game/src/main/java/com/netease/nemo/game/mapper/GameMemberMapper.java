package com.netease.nemo.game.mapper;

import com.netease.nemo.game.model.po.GameMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMemberMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameMember record);

    int insertSelective(GameMember record);

    GameMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameMember record);

    int updateByPrimaryKey(GameMember record);

    List<GameMember> selectGameMembers(@Param("roomUuid") String roomUuid, @Param("gameId") String gameId);

    List<GameMember> selectGameMembersById(@Param("gameRecordId") Long gameRecordId);

    int deleteGameMember(@Param("roomUuid") String roomUuid, @Param("gameId") String gameId);

    int deleteGameMemberById(@Param("gameRecordId") Long gameRecordId);

}