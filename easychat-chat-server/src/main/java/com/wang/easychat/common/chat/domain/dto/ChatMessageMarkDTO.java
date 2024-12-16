package com.wang.easychat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription: 标记消息请求
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkDTO {

    @ApiModelProperty("操作者")
    private Long uid;

    @ApiModelProperty("消息id")
    private Long msgId;

    /**
     * @see com.wang.easychat.common.chat.domain.enums.MessageMarkTypeEnum
     */
    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    /**
     * @see com.wang.easychat.common.chat.domain.enums.MessageMarkActTypeEnum
     */
    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}