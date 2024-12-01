package com.wang.easychat.common.chat.domain.vo.req.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassDescription: 文本消息入参
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgReq {

    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "消息内容过长")
    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("回复的消息id,如果没有别传就好")
    private Long replyMsgId;

    @ApiModelProperty("艾特的uid集合")
    @Size(max = 10, message = "一次最多可@十个用户")
    private List<Long> atUidList;
}
