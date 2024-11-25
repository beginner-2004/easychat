package com.wang.easychat.common.user.domain.vo.resp.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription: 好友校验
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp {
    @ApiModelProperty("好友id")
    private Long id;

    /**
     * @see com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

}
