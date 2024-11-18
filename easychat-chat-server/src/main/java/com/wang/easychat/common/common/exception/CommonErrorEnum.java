package com.wang.easychat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassDescription: 普通异常枚举
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum{
    BUSINESS_ERROR(0, "{0}"),
    SYSTEM_ERROR(-1, "系统开小差了，请稍后再试"),
    PARAM_INVALID(-2, "参数校验失败"),
    LOCK_LIMIT(-3, "请求太频繁了，请稍后再试"),
    ;

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
