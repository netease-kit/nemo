package com.netease.nemo.mapper;

import com.netease.nemo.model.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserUuid(@Param("userUuid") String userUuid);

    User getUserByMobile(@Param("mobile") String mobile);

    List<User> selectByUserUuids(@Param("userUuids") List<String> userUuids);
}