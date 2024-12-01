package com.wang.easychat.common.common.utils.discover.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlInfo {
    /**
     * 标题
     **/
    String title;

    /**
     * 描述
     **/
    String description;

    /**
     * 网站LOGO
     **/
    String image;

}
