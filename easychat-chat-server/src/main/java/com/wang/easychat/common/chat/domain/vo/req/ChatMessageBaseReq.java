package com.wang.easychat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription: 消息基础请求体
 * @Author:Wangzd
 * @Date: 2024/12/6
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageBaseReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
