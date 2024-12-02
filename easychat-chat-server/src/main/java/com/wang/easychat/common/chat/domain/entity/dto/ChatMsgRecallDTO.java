package com.wang.easychat.common.chat.domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassDescription: 消息撤回的推送类
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {
    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;
}
