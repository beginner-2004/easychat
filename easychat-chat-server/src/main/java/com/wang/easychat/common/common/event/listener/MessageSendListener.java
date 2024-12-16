package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.common.constant.MQConstant;
import com.wang.easychat.common.common.domain.dto.MsgSendMessageDTO;
import com.wang.easychat.common.common.event.MessageSendEvent;
import com.wang.easychat.transaction.service.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Date;

/**
 * @ClassDescription: 消息事件监听器
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@Component
@Slf4j
public class MessageSendListener {

    @Autowired
    private MQProducer mqProducer;

    // 涉及mq，需要在事务内，这里选择before_commit
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }

    // todo 艾特群机器人


}
