package com.wang.easychat.common.websocket.domain.vo.resp;

import com.wang.easychat.common.websocket.domain.enums.WSRespTypeEnum;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/7
 **/
public class WSBaseResp<T> {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
