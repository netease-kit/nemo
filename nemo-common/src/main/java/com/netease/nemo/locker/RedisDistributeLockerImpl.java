package com.netease.nemo.locker;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.function.Action;
import com.netease.nemo.function.ActionNoReturn;
import com.netease.nemo.util.RedisUtil;
import com.netease.nemo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service("redisDistributeLockerImpl")
@Slf4j
public class RedisDistributeLockerImpl implements LockerService {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Override
    public void lock(String key) {
        try {
            tryLock(key, 3, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.debug("lock failed.", e);
        }
    }

    @Override
    public void unlock(String key) {
        releaseLock(key);
    }

    @Override
    public void lock(String key, int timeout) {
        try {
            tryLock(key, timeout, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.debug("lock failed.", e);
        }
    }

    @Override
    public void lock(String key, int timeout, TimeUnit unit) {
        try {
            tryLock(key, timeout, 5, unit);
        } catch (InterruptedException e) {
            log.debug("lock failed.", e);
        }
    }

    @Override
    public boolean tryLock(String key) {
        try {
            return tryLock(key, 3, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        String value = UUIDUtil.getUUID().replaceAll("-", "").toLowerCase();
        Boolean flag = setNx(key, value, leaseTime, TimeUnit.SECONDS);
        // 尝试获取锁 成功返回
        if (flag == null) {
            return false;
        } else if (flag) {
            return true;
        }
        // 获取锁失败
        long newTime = System.currentTimeMillis();
        // 等待过期时间
        long expiredTime = newTime + waitTime * 1000;
        // 不断尝试获取锁成功返回
        while (System.currentTimeMillis() < expiredTime) {
            Boolean retryFlag = setNx(key, value, leaseTime, TimeUnit.SECONDS);
            if (retryFlag) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object tryLockAndDoAndReturn(Action action, Object... keyParts) {
        boolean lock = false;
        long start = System.nanoTime();
        String lockKey = RedisUtil.joinKey(keyParts);
        try {
            // TODO 业务自行配置 尝试获取时间及锁持有时间
            lock = this.tryLock(lockKey, 3, 5, TimeUnit.SECONDS);
            long end = System.nanoTime();

            if (!lock) {
                log.error("get Locker failed. the key:{}, cost ms: {}", lockKey, end - start);
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "操作频繁,请稍后重试!");
            }
            return action.doAction();
        } catch (InterruptedException e) {
            log.error("tryLockAndDoAndReturn failed.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "操作频繁,请稍后重试!");
        } finally {
            if (lock) {
                unlock(lockKey);
            }
        }
    }

    @Override
    public void tryLockAndDo(ActionNoReturn action, Object... keyParts) {
        boolean lock = false;
        long start = System.nanoTime();
        String lockKey = RedisUtil.joinKey(keyParts);
        try {
            // TODO 业务自行配置 尝试获取时间及锁持有时间
            lock = this.tryLock(lockKey, 3, 5, TimeUnit.SECONDS);
            long end = System.nanoTime();

            if (!lock) {
                log.error("get Locker failed. the key:{}, cost ms: {}", lockKey, end - start);
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "操作频繁,请稍后重试!");
            }
            action.doAction();
        } catch (InterruptedException e) {
            log.error("tryLockAndDoAndReturn failed.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "操作频繁,请稍后重试!");
        } finally {
            if (lock) {
                unlock(lockKey);
            }
        }
    }

    /**
     * 如果已经存在返回false，否则返回true
     *
     * @param key   key
     * @param value value
     * @return true-获得锁成功 false-获得锁失败
     */
    public Boolean setNx(String key, String value, Long expireTime, TimeUnit mimeUnit) {
        return nemoRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, mimeUnit);
    }

    /**
     * 释放锁
     *
     * @param key lockKey
     */
    private void releaseLock(String key) {
        nemoRedisTemplate.delete(key);
    }
}
