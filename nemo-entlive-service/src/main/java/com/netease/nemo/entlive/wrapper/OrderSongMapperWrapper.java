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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            @CacheEvict(key = "'OrderSongCount_' + 'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + 'liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
    })
    public int insert(OrderSong record) {
        return orderSongMapper.insert(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_' + 'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + 'liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
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
            @CacheEvict(key = "'OrderSongCount_' + 'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + 'liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
    })
    public int updateByPrimaryKeySelective(OrderSong record) {
        return orderSongMapper.updateByPrimaryKeySelective(record);
    }

    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_' + 'liveRecordId_' + #record.liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + 'liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #record.liveRecordId + '_userUuid_' + #record.userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #record.liveRecordId"),
    })
    public int updateByPrimaryKey(OrderSong record) {
        return orderSongMapper.updateByPrimaryKey(record);
    }

    @Cacheable(key = "'liveRecordId_' + #liveRecordId", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordId(Long liveRecordId) {
        return orderSongMapper.selectByLiveRecordId(liveRecordId);
    }

    @Cacheable(key = "'UserOrder_liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public List<OrderSong> selectByLiveRecordIdAndUserId(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectByLiveRecordIdAndUserId(liveRecordId, userUuid);
    }

    public int cleanOrderSongs(Long liveRecordId) {
        List<OrderSong> orderSongs = selectByLiveRecordId(liveRecordId);
        if (CollectionUtils.isEmpty(orderSongs)) {
            return 0;
        }

        // 批量删除key
        nemoRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (OrderSong orderSong : orderSongs) {
                String orderSongCountKey = RedisUtil.springCacheJoinKey("Order_Song", "OrderSongCount_" + orderSong.getLiveRecordId());
                String OrderSongUserCountKey = RedisUtil.springCacheJoinKey("Order_Song", "OrderSongCount_" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String OrderSongListKey = RedisUtil.springCacheJoinKey("Order_Song", "liveRecordId_" + orderSong.getLiveRecordId());
                String OrderSongUserListKey = RedisUtil.springCacheJoinKey("Order_Song", "UserOrder_liveRecordId_" + orderSong.getLiveRecordId() + "_userUuid_" + orderSong.getUserUuid());
                String idKey = RedisUtil.springCacheJoinKey("Order_Song", "id_" + orderSong.getId());

                deleteKey(connection, orderSongCountKey);
                deleteKey(connection, OrderSongUserCountKey);
                deleteKey(connection, OrderSongListKey);
                deleteKey(connection, OrderSongUserListKey);
                deleteKey(connection, idKey);
            }
            return null;
        });
        return orderSongMapper.cleanOrderSongs(liveRecordId);
    }


    @Caching(evict = {
            @CacheEvict(key = "'id_'.concat(#record.id)"),
            @CacheEvict(key = "'OrderSongCount_' + 'liveRecordId_' + #liveRecordId"),
            @CacheEvict(key = "'UserOrderSongCount_' + 'liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid"),
            @CacheEvict(key = "'UserOrder_liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid"),
            @CacheEvict(key = "'liveRecordId_' + #liveRecordId"),
    })
    public void cleanOrderSongsByUserUuid(Long liveRecordId, String userUuid) {
        orderSongMapper.cleanOrderSongsByUserUuid(liveRecordId, userUuid);
    }

    @Cacheable(key = "'OrderSongCount_' + 'liveRecordId_' + #liveRecordId", unless = "#result == null")
    public int selectOrderSongCount(Long liveRecordId) {
        return orderSongMapper.selectOrderSongCount(liveRecordId);
    }

    @Cacheable(key = "'UserOrderSongCount_' + 'liveRecordId_' + #liveRecordId + '_userUuid_' + #userUuid", unless = "#result == null")
    public int selectUserOrderSongCount(Long liveRecordId, String userUuid) {
        return orderSongMapper.selectUserOrderSongCount(liveRecordId, userUuid);
    }
}