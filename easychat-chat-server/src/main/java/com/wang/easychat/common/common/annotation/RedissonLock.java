package com.wang.easychat.common.common.annotation;

import javax.validation.groups.Default;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @ClassDescription: 分布式锁注解
 * @Author:Wangzd
 * @Date: 2024/11/17
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {

    /**
     * key的前缀，默认取方法全限定名，可以自己指定
     * @return
     */
    String prefixKey() default "";

    /**
     * 支持spring的el表达式的key
     * @return
     */
    String key();


    /**
     * 等待锁的排队时间，默认快速失败，失败直接拒绝
     * @return
     */
    int waitTime() default -1;

    /**
     * 时间单位，默认毫秒
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
