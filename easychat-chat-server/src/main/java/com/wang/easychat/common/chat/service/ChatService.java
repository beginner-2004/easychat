package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.dto.MsgReadInfoDTO;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.vo.req.*;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.ChatMemberResp;

import java.util.Collection;
import java.util.List;

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

    /**
     * 标记消息
     * @param uid
     * @param request
     */
    void setMsgMark(Long uid, ChatMessageMarkReq request);


    /**
     * 查询消息已读情况
     * @param uid
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request);

    /**
     * 读取消息
     * @param uid
     * @param request
     */
    void msgRead(Long uid, ChatMessageMemberReq request);

    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request);

    /**
     * 获取群成员列表
     * @param memberUidList
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request);
}
