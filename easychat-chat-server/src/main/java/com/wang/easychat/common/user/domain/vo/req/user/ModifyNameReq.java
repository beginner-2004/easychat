package com.wang.easychat.common.user.domain.vo.req.user;

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
public class ModifyNameReq {
    @ApiModelProperty("新用户名")
    @NotBlank(message = "修改的用户名不能为空")
    @Length(max = 6, message = "用户名不能超过六个字")
    private String name;
}
