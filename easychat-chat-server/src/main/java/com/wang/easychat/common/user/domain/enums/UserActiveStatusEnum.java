package com.wang.easychat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/16
 **/
@AllArgsConstructor
@Getter
public enum UserActiveStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    ;

    private final Integer status;
    private final String desc;
}
