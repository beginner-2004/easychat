package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;

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
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    /**
     * 获取会话消息
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long receiveUid);

    /**
     * 撤回消息
     */
    void recallMsg(Long uid, ChatMessageBaseReq request);
}
