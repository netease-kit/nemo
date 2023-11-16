package com.netease.nemo.game.mapper;

import com.netease.nemo.game.model.po.GameRecordHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameRecordHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameRecordHistory record);

    int insertSelective(GameRecordHistory record);

    GameRecordHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameRecordHistory record);

    int updateByPrimaryKey(GameRecordHistory record);
}