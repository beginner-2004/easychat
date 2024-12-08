package com.wang.easychat.transaction.service;

import java.util.Objects;

/**
 * @ClassDescription: 用于存储当前的执行状态
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
public class SecureInvokeHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    public static boolean isInvoking() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    public static void setInvoking() {
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    public static void invoked() {
        INVOKE_THREAD_LOCAL.remove();
    }
}
