package com.wang.easychat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription: 消息请求
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {
    @NotNull
    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("消息类型")
    @NotNull
    private Integer msgType;

    /**
     * @see com.wang.easychat.common.chat.domain.entity.msg
     */
    @ApiModelProperty("消息内容，类型不同传值不同")
    @NotNull
    private Object body;

}
