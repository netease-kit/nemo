package com.netease.nemo.entlive.mapper;

import com.netease.nemo.entlive.model.po.ChorusRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChorusRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChorusRecord record);

    int insertSelective(ChorusRecord record);

    ChorusRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChorusRecord record);

    int updateByPrimaryKey(ChorusRecord record);

    ChorusRecord selectByChorusId(@Param("chorusId") String chorusId);

    List<ChorusRecord> selectNotEndChorusRecord(@Param("roomUuid") String roomUuid);

    List<ChorusRecord> selectOrderSongChorusRecord(@Param("roomUuid") String roomUuid, @Param("orderId") Long orderId);
}