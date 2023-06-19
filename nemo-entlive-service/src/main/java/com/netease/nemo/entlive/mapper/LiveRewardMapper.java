package com.netease.nemo.entlive.mapper;

import com.netease.nemo.entlive.dto.LiveRewardTotalDto;
import com.netease.nemo.entlive.model.po.LiveReward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveRewardMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LiveReward record);

    int insertSelective(LiveReward record);

    LiveReward selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LiveReward record);

    int updateByPrimaryKey(LiveReward record);

    LiveRewardTotalDto countRewardTotal(@Param("liveRecordId") Long liveRecordId);

    LiveRewardTotalDto countUserRewardTotal(@Param("liveRecordId") Long liveRecordId, @Param("target") String target);

    int batchInsert(@Param("pojos") List<LiveReward> liveRewards);
}