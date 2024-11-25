package com.wang.easychat.common.user.domain.vo.req.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@Data
public class BlackReq {
    @ApiModelProperty("拉黑用户的uid")
    @NotNull
    private Long uid;
}
