package com.wang.easychat.common.user.domain.dto;

import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassDescription: 解散群聊消息体
 * @Author:Wangzd
 * @Date: 2024/12/16
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDelDTO {

    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("群组id")
    private Long groupId;


}
