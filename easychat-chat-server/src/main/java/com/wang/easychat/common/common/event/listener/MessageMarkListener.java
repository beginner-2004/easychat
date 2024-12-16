package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.service.IMessageMarkService;
import com.wang.easychat.common.chat.service.IMessageService;
import com.wang.easychat.common.common.event.MessageMarkEvent;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import com.wang.easychat.common.user.service.impl.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Slf4j
@Component
public class MessageMarkListener {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IMessageMarkService messageMarkService;
    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkDTO dto = event.getDto();
        Message msg = messageService.getById(dto.getMsgId());
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) {//普通消息才需要升级
            return;
        }
        //消息被标记次数
        Integer markCount = messageMarkService.getMarkCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        if (MessageMarkTypeEnum.LIKE.getType().equals(dto.getMarkType())) {//尝试给用户发送一张徽章
            userBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdemporentEnum.MSG_ID, msg.getId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event) { // 后续可做合并查询，目前异步影响不大
        ChatMessageMarkDTO dto = event.getDto();
        Integer markCount = messageMarkService.getMarkCount(dto.getMsgId(), dto.getMarkType());
        pushService.sendPushMsg(WSAdapter.buildMsgMarkSend(dto, markCount));
    }
}
