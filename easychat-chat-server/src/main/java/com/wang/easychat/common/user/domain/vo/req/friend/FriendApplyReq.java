package com.wang.easychat.common.user.domain.vo.req.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyReq {

    @NotBlank
    @ApiModelProperty("申请信息")
    private String msg;

    @NotNull
    @ApiModelProperty("好友uid")
    private Long targetUid;

}
