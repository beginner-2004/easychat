package com.wang.easychat.common.chat.service.strategy.msg;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.entity.dto.ChatMsgRecallDTO;
import com.wang.easychat.common.chat.domain.entity.msg.MessageExtra;
import com.wang.easychat.common.chat.domain.entity.msg.MsgRecall;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageService;
import com.wang.easychat.common.chat.service.cache.MsgCache;
import com.wang.easychat.common.common.event.MessageRecallEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object> {
    @Autowired
    private IMessageService messageService;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userInfoCache.get(recall.getRecallUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) {// todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();

        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageService.updateById(update);
        // 发送艾特用户事件
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));

    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
}
