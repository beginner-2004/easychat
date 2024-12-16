package com.wang.easychat.common.chat.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription: 消息已读未读出参
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadResp {
    @ApiModelProperty("已读或者未读的用户uid")
    private Long uid;
}
