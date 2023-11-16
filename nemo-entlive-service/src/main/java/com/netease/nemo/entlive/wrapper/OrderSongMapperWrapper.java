package com.netease.nemo.entlive.wrapper;

import com.netease.nemo.entlive.mapper.OrderSongMapper;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@CacheConfig(cacheNames = "Order_Song")
public class OrderSongMapperWrapper extends BaseWrapper {

    @Resource
    private OrderSongMapper orderSongMapper;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_'  + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'OrderSongCount_ktv_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_ktv_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'ktv_UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'ktv_liveRecordId_' + #record.liveRecordId"),
    })
    public int insert(OrderSong record) {
        return orderSongMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'OrderSongCount_ktv_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_ktv_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'ktv_UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'ktv_liveRecordId_' + #record.liveRecordId"),
    })
    public int insertSelective(OrderSong record) {
        return orderSongMapper.insertSelective(record);
    }

    @Cacheable(key = "'id_' + #id", unless = "#result == null")
    public OrderSong selectByPrimaryKey(Long id) {
        return orderSongMapper.selectByPrimaryKey(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'OrderSongCount_ktv_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_ktv_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'ktv_UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'ktv_liveRecordId_' + #record.liveRecordId"),
    })
    public int updateByPrimaryKeySelective(OrderSong record) {
        return orderSongMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_'  + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'OrderSongCount_ktv_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_ktv_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'ktv_UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'ktv_liveRecordId_' + #record.liveRecordId"),
    })
    public int updateByPrimaryKey(OrderSong record) {
        return orderSongMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'liveRecordId_' + #liveRecordId", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordId(Long liveRecordId) {
        return orderSongMapper.selectByLiveRecordId(liveRecordId);
    }

    @Cacheable(key = "'ktv_liveRecordId_' + #liveRecordId", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordIdForKtv(Long liveRecordId) {
        return orderSongMapper.selectByLiveRecordIdForKtv(liveRecordId);
    }

    @Cacheable(key = "'UserOrder_liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordIdAndUserId(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectByLiveRecordIdAndUserId(liveRecordId, userUuid);
    }

    @Cacheable(key = "'ktv_UserOrder_liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordIdAndUserIdForKtv(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectByLiveRecordIdAndUserIdForKtv(liveRecordId, userUuid);
    }

    public int cleanOrderSongs(Long liveRecordId) {
        List<OrderSong> orderSongs = selectByLiveRecordId(liveRecordId);
        if (CollectionUtils.isEmpty(orderSongs)) {
            return 0;
        }

        cleanRedisKey(orderSongs);
        return orderSongMapper.cleanOrderSongs(liveRecordId);
    }

    private void cleanRedisKey(List<OrderSong> orderSongs) {
        // 批量删除key
        nemoRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (OrderSong orderSong : orderSongs) {
                String orderSongCountKey = RedisUtil.springCacheJoinKey("Order_Song", "OrderSongCount_" + orderSong.getLiveRecordId());
                String ktvOrderSongCountKey = RedisUtil.springCacheJoinKey("Order_Song", "OrderSongCount_ktv_" + orderSong.getLiveRecordId());
                String OrderSongUserCountKey = RedisUtil.springCacheJoinKey("Order_Song", "UserOrderSongCount_" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String ktvOrderSongUserCountKey = RedisUtil.springCacheJoinKey("Order_Song", "UserOrderSongCount_ktv" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String OrderSongListKey = RedisUtil.springCacheJoinKey("Order_Song", "liveRecordId_" + orderSong.getLiveRecordId());
                String ktvOrderSongListKey = RedisUtil.springCacheJoinKey("Order_Song", "ktv_liveRecordId_" + orderSong.getLiveRecordId());
                String OrderSongUserListKey = RedisUtil.springCacheJoinKey("Order_Song", "UserOrder_liveRecordId_" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String KtvOrderSongUserListKey = RedisUtil.springCacheJoinKey("Order_Song", "ktv_UserOrder_liveRecordId_" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String idKey = RedisUtil.springCacheJoinKey("Order_Song", "id_" + orderSong.getId());

                deleteKey(connection, orderSongCountKey);
                deleteKey(connection, ktvOrderSongCountKey);
                deleteKey(connection, OrderSongUserCountKey);
                deleteKey(connection, ktvOrderSongUserCountKey);
                deleteKey(connection, OrderSongListKey);
                deleteKey(connection, ktvOrderSongListKey);
                deleteKey(connection, OrderSongUserListKey);
                deleteKey(connection, KtvOrderSongUserListKey);
                deleteKey(connection, idKey);
            }
            return null;
        });
    }

    public int cleanOrderSongsByUserUuid(Long liveRecordId, String userUuid) {
        List<OrderSong> orderSongs = selectByLiveRecordIdAndUserId(liveRecordId, userUuid);
        if (CollectionUtils.isEmpty(orderSongs)) {
            return 0;
        }
        cleanRedisKey(orderSongs);
        return orderSongMapper.cleanOrderSongsByUserUuid(liveRecordId, userUuid);
    }

    @Cacheable(key = "'OrderSongCount_' + 'liveRecordId_' + #liveRecordId", unless = "#result == null")
    public int selectOrderSongCount(Long liveRecordId) {
        return orderSongMapper.selectOrderSongCount(liveRecordId);
    }

    @Cacheable(key = "'UserOrderSongCount_' + 'liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public int selectUserOrderSongCount(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectUserOrderSongCount(liveRecordId, userUuid);
    }

    @Cacheable(key = "'OrderSongCount_ktv_'  + #liveRecordId", unless = "#result == null")
    public int selectOrderSongCountForKtv(Long liveRecordId) {
        return orderSongMapper.selectOrderSongCountForKtv(liveRecordId);
    }

    @Cacheable(key = "'UserOrderSongCount_ktv_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public int selectUserOrderSongCountForKtv(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectUserOrderSongCount(liveRecordId, userUuid);
    }
}