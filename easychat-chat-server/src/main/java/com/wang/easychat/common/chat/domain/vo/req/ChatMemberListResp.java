package com.wang.easychat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @ClassDescription: @用户列表返回体
 * @Author:Wangzd
 * @Date: 2024/12/16
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberListResp {
    @ApiModelProperty("uid")
    private Long uid;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("头像")
    private String avatar;
}
