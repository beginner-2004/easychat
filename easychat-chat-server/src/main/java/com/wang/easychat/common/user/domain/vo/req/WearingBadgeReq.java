package com.wang.easychat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@Data
public class WearingBadgeReq {
    @ApiModelProperty("徽章id")
    @NotNull
    private Long itemId;
}
