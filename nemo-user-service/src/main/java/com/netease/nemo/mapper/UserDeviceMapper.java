package com.netease.nemo.mapper;

import com.netease.nemo.model.po.UserDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDeviceMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserDevice record);

    int insertSelective(UserDevice record);

    UserDevice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDevice record);

    int updateByPrimaryKey(UserDevice record);

    UserDevice selectByUserAndDeviceId(@Param("userUuid") String userUuid, @Param("deviceId")String deviceId);
}