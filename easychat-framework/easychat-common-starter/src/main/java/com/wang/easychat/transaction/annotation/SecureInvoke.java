package com.wang.easychat.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassDescription: 保证方法成功执行，如果在事务内的方法，会将操作记录入库，保证执行
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecureInvoke {
    /**
     * 默认三次
     */
    int maxRetryTimes() default 3;

    /**
     * 默认异步执行，不影响主线快速返回
     */
    boolean async() default true;
}
