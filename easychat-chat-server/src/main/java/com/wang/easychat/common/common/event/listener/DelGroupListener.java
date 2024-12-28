package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.chat.service.adapter.MemberAdapter;
import com.wang.easychat.common.chat.service.adapter.RoomAdapter;
import com.wang.easychat.common.common.event.DelGroupEvent;
import com.wang.easychat.common.common.event.FriendApplyEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.impl.PushService;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * @ClassDescription: 解散群聊监听器
 * @Author:Wangzd
 * @Date: 2024/12/16
 **/
@Component
@Slf4j
public class DelGroupListener {
    @Autowired
    private ChatService chatService;
    @Autowired
    private PushService pushService;

    /**
     * 向群中发送解散群聊消息
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = DelGroupEvent.class, fallbackExecution = true)
    public void sendDelMsg(DelGroupEvent event) {
        Long roomId = event.getGroupDelDTO().getRoomId();
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupDelMessage(roomId);
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }


}
