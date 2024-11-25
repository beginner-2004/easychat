package com.wang.easychat.common.common.aspect;

import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.service.LockService;
import com.wang.easychat.common.common.utils.SpElUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @ClassDescription: @RedissonLock注解 切面业务类
 * @Author:Wangzd
 * @Date: 2024/11/17
 **/
@Component
@Aspect
@Order(0)   // 确保比事务的注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    @Autowired
    private LockService lockService;

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.excuteWithLock(prefix + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }

}
