package com.wang.easychat.common.common.service;

import com.wang.easychat.common.common.exception.BusinessException;
import com.wang.easychat.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @ClassDescription: 分布式锁工具类
 * @Author:Wangzd
 * @Date: 2024/11/17
 **/
@Service
public class LockService {
    @Autowired
    private RedissonClient redissonClient;


    @SneakyThrows
    public <T> T excuteWithLock(String key, int waitTime, TimeUnit timeUnit, Supplier<T> supplier){
        RLock lock = redissonClient.getLock(key);
        boolean isSuccess = lock.tryLock(waitTime, timeUnit);
        if (!isSuccess){
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try{
            return supplier.get();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 无等待时间
     * @param key
     * @param supplier
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T> T excuteWithLock(String key, Supplier<T> supplier){
        return excuteWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }

    @SneakyThrows
    public <T> T excuteWithLock(String key, Runnable runnable){
        return excuteWithLock(key, -1, TimeUnit.MILLISECONDS, ()->{
            runnable.run();
            return null;
        });
    }

    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

}
