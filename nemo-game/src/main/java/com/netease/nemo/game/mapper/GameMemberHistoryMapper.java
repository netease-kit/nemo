package com.netease.nemo.game.mapper;

import com.netease.nemo.game.model.po.GameMemberHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMemberHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameMemberHistory record);

    int insertSelective(GameMemberHistory record);
    GameMemberHistory selectByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(GameMemberHistory record);

    int updateByPrimaryKey(GameMemberHistory record);

    int insertList(@Param("pojos") List<GameMemberHistory> pojo);
}