package com.wang.easychat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2025/2/5
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDelReq {
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

    @NotNull
    @ApiModelProperty("被移除的uid（主动退群填自己）")
    private Long uid;
}
