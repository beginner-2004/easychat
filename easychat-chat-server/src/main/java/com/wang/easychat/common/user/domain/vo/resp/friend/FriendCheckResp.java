package com.wang.easychat.common.user.domain.vo.resp.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendCheckResp {

    @ApiModelProperty("校验结果")
    private List<FriendCheck> checkedList;

    @Data
    public static class FriendCheck {
        private Long uid;
        private Boolean isFriend;
    }

}
