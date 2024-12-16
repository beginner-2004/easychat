package com.wang.easychat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription: 确定哪个好友
 * @Author:Wangzd
 * @Date: 2024/12/13
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactFriendReq {

    @NotNull
    @ApiModelProperty("好友uid")
    private Long uid;
}
