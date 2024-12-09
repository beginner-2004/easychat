package com.wang.easychat.common.user.domain.vo.resp.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiResp {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 表情地址
     */
    @ApiModelProperty(value = "表情url")
    private String expressionUrl;

}
