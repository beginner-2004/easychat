package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {
    @Autowired
    private IMessageService messageService;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void saveMsg(Message msg, VideoMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageService.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "视频";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[视频]";
    }
}
