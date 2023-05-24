package com.netease.nemo.mapper;

import com.netease.nemo.model.po.UserReward;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRewardMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserReward record);

    int insertSelective(UserReward record);

    UserReward selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserReward record);

    int updateByPrimaryKey(UserReward record);
}