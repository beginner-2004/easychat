package com.wang.easychat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/15
 **/
@AllArgsConstructor
@Getter
public enum YesOrNoEnum {
    NO(0, "否"),
    YES(1, "是"),
    ;


    private final Integer status;
    private final String desc;
}
