package com.netease.nemo.entlive.mapper;

import com.netease.nemo.entlive.model.po.LiveRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LiveRecord record);

    int insertSelective(LiveRecord record);

    LiveRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LiveRecord record);

    int updateByPrimaryKey(LiveRecord record);

    LiveRecord selectByUserUuidAndType(@Param("userUuid") String host, @Param("liveType") Integer liveType);

    LiveRecord selectByRoomArchiveId(@Param("roomArchiveId") String roomArchiveId);

    LiveRecord selectByUserUuid(@Param("userUuid") String userUuid);

    List<LiveRecord> getLivingRecords(@Param("liveType") Integer liveType, int live, String excludeUserUuid);
}