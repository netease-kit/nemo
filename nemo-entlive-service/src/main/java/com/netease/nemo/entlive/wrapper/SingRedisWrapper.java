package com.netease.nemo.entlive.wrapper;

import com.netease.nemo.entlive.dto.SingBaseInfoDto;
import com.netease.nemo.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.netease.nemo.enums.RedisKeyEnum.ENT_KTV_SING_BASE_INFO;

@Component
public class SingRedisWrapper {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    public SingBaseInfoDto getSingBaseInfo(String roomUuid) {
        if (StringUtils.isAnyEmpty(roomUuid)) {
            return null;
        }

        String cacheKey = RedisUtil.joinKey(ENT_KTV_SING_BASE_INFO.getKeyPrefix(), roomUuid);
        return (SingBaseInfoDto) nemoRedisTemplate.opsForValue().get(cacheKey);
    }

    public void setSingBaseInfo(String roomUuid, SingBaseInfoDto singBaseInfoDto) {
        if (StringUtils.isAnyEmpty(roomUuid)) {
            return;
        }
        if (singBaseInfoDto == null) {
            return;
        }

        String cacheKey = RedisUtil.joinKey(ENT_KTV_SING_BASE_INFO.getKeyPrefix(), roomUuid);
        nemoRedisTemplate.opsForValue().set(cacheKey, singBaseInfoDto, 1, TimeUnit.DAYS);
    }

    public void delSingBaseInfo(String roomUuid) {
        if (StringUtils.isAnyEmpty(roomUuid)) {
            return;
        }

        String cacheKey = RedisUtil.joinKey(ENT_KTV_SING_BASE_INFO.getKeyPrefix(), roomUuid);
        nemoRedisTemplate.delete(cacheKey);
    }
}
