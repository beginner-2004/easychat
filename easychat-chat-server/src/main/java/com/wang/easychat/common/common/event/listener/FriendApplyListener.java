package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.common.event.FriendApplyEvent;
import com.wang.easychat.common.common.event.MessageMarkEvent;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/12
 **/
@Slf4j
@Component
public class FriendApplyListener {

    @Autowired
    private ChatService chatService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = FriendApplyEvent.class, fallbackExecution = true)
    public void sendMsg(FriendApplyEvent event) { // 后续可做合并查询，目前异步影响不大
        chatService.sendMsg(event.getDto().getChatMessageReq(), event.getDto().getUid());
    }
}
