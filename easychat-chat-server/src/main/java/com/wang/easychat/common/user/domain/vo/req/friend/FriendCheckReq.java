package com.wang.easychat.common.user.domain.vo.req.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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
public class FriendCheckReq {

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty("校验好友的uid")
    private List<Long> uidList;

}
