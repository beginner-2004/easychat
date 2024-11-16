package com.wang.easychat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/14
 **/
@Data
@ApiModel("徽章详情")
public class BadgeResp {

    @ApiModelProperty(value = "徽章id")
    private Long id;

    @ApiModelProperty(value = "徽章图标")
    private String img;

    @ApiModelProperty(value = "徽章描述")
    private String describe;

    @ApiModelProperty(value = "是否拥有 0否 1是")
    private Integer obtain;

    @ApiModelProperty(value = "是否佩戴 0否 1是")
    private Integer wearing;

}
