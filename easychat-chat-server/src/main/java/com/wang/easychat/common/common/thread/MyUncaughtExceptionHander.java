package com.wang.easychat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/12
 **/
@Slf4j
public class MyUncaughtExceptionHander implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread", e);
    }
}
