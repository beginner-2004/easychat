package com.wang.easychat.common.user.domain.dto;

import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/12
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyDTO {

    @ApiModelProperty("消息体")
    private ChatMessageReq chatMessageReq;

    @ApiModelProperty("被同意申请的uid")
    private Long uid;

}
