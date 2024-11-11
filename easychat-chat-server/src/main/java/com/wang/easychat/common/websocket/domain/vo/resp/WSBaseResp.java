package com.wang.easychat.common.websocket.domain.vo.resp;

import com.wang.easychat.common.websocket.domain.enums.WSRespTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/7
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
