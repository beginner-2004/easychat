package com.wang.easychat.common.common.utils;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/19
 **/
public class DeepSeekThreadLocalUtil {
    private static final ThreadLocal<Boolean> DEEP_SEEK_THREAD = new ThreadLocal<>();

    /**
     * 设置deepseek线程
     */
    public static void setIsDeepSeekThread() {
        DEEP_SEEK_THREAD.set(true);
    }

    /**
     * 获取是否deepseek线程
     * @return
     */
    public static Boolean getDeepSeekThread() {
        return DEEP_SEEK_THREAD.get();
    }

    /**
     * 清理deepseek线程
     */
    public static void removeIsDeepSeekThread() {
        DEEP_SEEK_THREAD.remove();
    }


}
