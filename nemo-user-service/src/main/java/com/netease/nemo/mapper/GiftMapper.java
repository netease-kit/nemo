package com.netease.nemo.mapper;

import com.netease.nemo.model.po.Gift;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GiftMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Gift record);

    int insertSelective(Gift record);

    Gift selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Gift record);

    int updateByPrimaryKey(Gift record);
}