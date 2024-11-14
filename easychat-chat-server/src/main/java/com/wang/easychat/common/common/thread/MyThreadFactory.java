package com.wang.easychat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/12
 **/
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    public static final MyUncaughtExceptionHander MY_UNCAUGHT_EXCEPTION_HANDER = new MyUncaughtExceptionHander();

    private ThreadFactory original;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);  // 执行spring自己的创建逻辑
        // 额外装饰我们需要的创建逻辑
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDER);
        return thread;
    }
}
