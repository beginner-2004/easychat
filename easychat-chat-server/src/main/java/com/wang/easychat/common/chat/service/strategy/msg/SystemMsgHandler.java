package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemMsgHandler extends AbstractMsgHandler<String> {

    @Autowired
    private IMessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    public void saveMsg(Message msg, String body) {
        Message update = new Message();
        update.setId(msg.getId());
        update.setContent(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return msg.getContent();
    }
}
