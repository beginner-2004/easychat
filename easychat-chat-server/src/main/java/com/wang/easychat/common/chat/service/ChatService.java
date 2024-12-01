package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;

/**
    @ClassDescription:
    @Author:Wangzd
    @Date: 2024/11/29
**/

public interface ChatService {

    /**
     * 发送消息
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 整合消息展示体给前端
     */
    ChatMessageResp getMsgResp(Long msgId, Long uid);

}
