package com.netease.nemo.locker;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.function.Action;
import com.netease.nemo.function.ActionNoReturn;
import com.netease.nemo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis版本在2.6且开启
 */
@Service("redissonDistributeLockerImpl")
@Slf4j
public class RedissonDistributeLockerImpl implements LockerService {

    @Resource(name = "nemoRedissonClient")
    private RedissonClient redissonClient;

    @Override
    public void lock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.lock();
    }

    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }

    @Override
    public void lock(String key, int timeout) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void lock(String key, int timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(timeout, unit);
    }

    @Override
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock(waitTime, leaseTime, unit);
    }

    @Override
    public <T> T tryLockAndDoAndReturn(Action<T> action, Object... keyParts) {
        boolean lock = false;
        long start = System.nanoTime();
        String lockKey = RedisUtil.joinKey(keyParts);
        try {
            // TODO 业务自行配置 尝试获取时间及锁持有时间
            lock = this.tryLock(lockKey, 10, 10, TimeUnit.SECONDS);
            long end = System.nanoTime();

            if (!lock) {
                log.error("get Locker failed. the key:{}, cost ms: {}", lockKey, end - start);
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            return action.doAction();
        } catch (InterruptedException e) {
            log.error("tryLockAndDoAndReturn failed.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
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
            lock = this.tryLock(lockKey, 10, 10, TimeUnit.SECONDS);
            long end = System.nanoTime();

            if (!lock) {
                log.error("get Locker failed. the key:{}, cost ms: {}", lockKey, end - start);
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            action.doAction();
        } catch (InterruptedException e) {
            log.error("tryLockAndDoAndReturn failed.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (lock) {
                unlock(lockKey);
            }
        }
    }
}
