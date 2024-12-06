package com.wang.easychat.common.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSendMessageDTO implements Serializable {
    private Long msgId;
}
