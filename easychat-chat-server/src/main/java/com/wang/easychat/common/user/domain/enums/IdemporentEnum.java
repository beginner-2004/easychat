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
public enum IdemporentEnum {
    UID(1, "uid"),
    MSG_ID(2, "消息id"),
    ;

    private final Integer type;
    private final String desc;
}
