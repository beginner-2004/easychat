package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.chat.domain.entity.dto.ChatMsgRecallDTO;
import com.wang.easychat.common.chat.service.cache.MsgCache;
import com.wang.easychat.common.common.event.MessageRecallEvent;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import com.wang.easychat.common.user.service.impl.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @ClassDescription: 消息回复事件监听器
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Component
@Slf4j
public class MessageRecallListener {
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private PushService pushService;


    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event){
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event){
        pushService.sendPushMsg(WSAdapter.buildMsgRecall(event.getRecallDTO()));
    }
}
