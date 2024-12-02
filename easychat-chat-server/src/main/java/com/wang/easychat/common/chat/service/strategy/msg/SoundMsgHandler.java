package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.entity.msg.SoundMsgDTO;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SoundMsgHandler extends AbstractMsgHandler<SoundMsgDTO> {
    @Autowired
    private IMessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SOUND;
    }

    @Override
    public void saveMsg(Message msg, SoundMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setSoundMsgDTO(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getSoundMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "语音";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[语音]";
    }
}
