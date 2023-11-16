package com.netease.nemo.entlive.mapper;

import com.netease.nemo.entlive.model.po.OrderSong;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderSongMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderSong record);

    int insertSelective(OrderSong record);

    OrderSong selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderSong record);

    int updateByPrimaryKey(OrderSong record);

    List<OrderSong> selectByLiveRecordId(@Param("liveRecordId") Long liveRecordId);

    List<OrderSong> selectByLiveRecordIdForKtv(@Param("liveRecordId") Long liveRecordId);

    List<OrderSong> selectByLiveRecordIdAndUserId(@Param("liveRecordId") Long liveRecordId, @Param("userUuid") String userUuid);

    List<OrderSong> selectByLiveRecordIdAndUserIdForKtv(@Param("liveRecordId") Long liveRecordId, @Param("userUuid") String userUuid);

    int cleanOrderSongs(@Param("liveRecordId") Long liveRecordId);

    int cleanOrderSongsByUserUuid(@Param("liveRecordId") Long liveRecordId, @Param("userUuid") String userUuid);

    int selectOrderSongCount(@Param("liveRecordId") Long liveRecordId);

    int selectUserOrderSongCount(@Param("liveRecordId") Long liveRecordId,  @Param("userUuid") String userUuid);

    int selectOrderSongCountForKtv(@Param("liveRecordId") Long liveRecordId);
}