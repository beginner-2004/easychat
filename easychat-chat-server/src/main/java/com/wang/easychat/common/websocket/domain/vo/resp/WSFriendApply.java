package com.wang.easychat.common.websocket.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/7
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendApply {
    @ApiModelProperty("申请人")
    private Long uid;
    @ApiModelProperty("申请未读数")
    private Integer unreadCount;
}
