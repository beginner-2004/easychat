package com.wang.easychat.common.common.utils;

import com.wang.easychat.common.common.domain.dto.RequestInfo;

/**
 * @ClassDescription: 请求上下文
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
public class RequestHolder {
    public static final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<RequestInfo>();

    public static void set(RequestInfo requestInfo){
        threadLocal.set(requestInfo);
    }

    public static RequestInfo get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
