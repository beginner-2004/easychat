package com.wang.easychat.common.common.exception;

import lombok.Data;
import lombok.Getter;

/**
 * @ClassDescription: 自定义业务异常
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@Data
public class BusinessException extends RuntimeException{

    protected Integer errorCode;
    protected String errorMsg;

    public BusinessException(String errorMsg){
        super(errorMsg);
        this.errorCode = CommonErrorEnum.BUSINESS_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg){
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
