package com.wang.easychat.transaction.annotation;

import org.springframework.lang.Nullable;

import java.util.concurrent.Executor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
public interface SecureInvokeConfigurer {

    /**
     * 返回一个线程池
     */
    @Nullable
    default Executor getSecureInvokeExecutor() {
        return null;
    }

}
