package com.wang.easychat.common.common.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO {
    @ApiModelProperty("id")
    @NotNull
    private long id;
}
