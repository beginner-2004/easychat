package com.wang.easychat.common.websocket.domain.vo.req;

import com.wang.easychat.common.websocket.domain.enums.WSReqTypeEnum;
import lombok.Data;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/7
 **/
@Data
public class WSBaseReq {
    /**
     * @see WSReqTypeEnum
     */
    private Integer type;
    private String data;
}
