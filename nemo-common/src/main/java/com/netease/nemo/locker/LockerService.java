package com.netease.nemo.locker;

import com.netease.nemo.function.Action;
import com.netease.nemo.function.ActionNoReturn;

import java.util.concurrent.TimeUnit;

public interface LockerService {

    /**
     * 加锁
     *
     * @param key key
     */
    void lock(String key);

    /**
     * 释放锁
     *
     * @param key key
     */
    void unlock(String key);

    /**
     * 加锁锁,设置有效期
     *
     * @param key     key
     * @param timeout 有效时间，默认时间单位在实现类传入
     */
    void lock(String key, int timeout);

    /**
     * 加锁，设置有效期并指定时间单位
     *
     * @param key     key
     * @param timeout 有效时间
     * @param unit    时间单位
     */
    void lock(String key, int timeout, TimeUnit unit);

    /**
     * 尝试获取锁，获取到则持有该锁返回true,未获取到立即返回false
     *
     * @param key key
     * @return true-获取锁成功 false-获取锁失败
     */
    boolean tryLock(String key);

    /**
     * 尝试获取锁，获取到则持有该锁leaseTime时间.
     * 若未获取到，在waitTime时间内一直尝试获取，超过waitTime还未获取到则返回false
     *
     * @param key       key
     * @param waitTime  尝试获取时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @return true-获取锁成功 false-获取锁失败
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;


    /**
     * 加锁后执行Action并返回结果
     *
     * @param action   要执行的action函数
     * @param keyParts 锁key
     * @return 返回值
     */
    <T> T tryLockAndDoAndReturn(Action<T> action, Object... keyParts);

    /**
     * 加锁后执行Action
     *
     * @param action   要执行的action函数
     * @param keyParts 锁key
     */
    void tryLockAndDo(ActionNoReturn action, Object... keyParts);

}
