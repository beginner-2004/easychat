package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.msg.FileMsgDTO;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {
    @Autowired
    private IMessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    @Override
    public void saveMsg(Message msg, FileMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setFileMsg(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getFileMsg();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "文件:" + msg.getExtra().getFileMsg().getFileName();
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[文件]" + msg.getExtra().getFileMsg().getFileName();
    }
}
