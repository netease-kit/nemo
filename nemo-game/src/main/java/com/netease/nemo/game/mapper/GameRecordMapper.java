package com.netease.nemo.game.mapper;

import com.netease.nemo.game.model.po.GameRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameRecord record);

    int insertSelective(GameRecord record);

    GameRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameRecord record);

    int updateByPrimaryKey(GameRecord record);

    GameRecord selectByRoomUuid(@Param("roomUuid") String roomUuid);
}