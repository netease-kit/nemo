package com.netease.nemo.game.mapper;

import com.netease.nemo.game.model.po.GameReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameReportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameReport record);

    int insertSelective(GameReport record);

    GameReport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameReport record);

    int updateByPrimaryKey(GameReport record);
}