package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.msg.EmojisMsgDTO;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO>{
    @Autowired
    private IMessageService messageService;

    /**
     * 消息类型
     */
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    /**
     * 子类拓展保存消息逻辑
     *
     * @param msg
     * @param body
     */
    @Override
    protected void saveMsg(Message msg, EmojisMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setEmojisMsgDTO(body);
        messageService.updateById(update);
    }

    /**
     * 展示消息
     *
     * @param message
     */
    @Override
    public Object showMsg(Message message) {
        return message.getExtra().getEmojisMsgDTO();
    }

    /**
     * 展示被回复的消息
     *
     * @param msg
     */
    @Override
    public Object showReplyMsg(Message msg) {
        return "表情";
    }

    /**
     * 会话列表消息
     *
     * @param msg
     */
    @Override
    public String showContactMsg(Message msg) {
        return "[表情包]";
    }
}
