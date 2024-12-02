package com.wang.easychat.common.common.event;

import com.wang.easychat.common.chat.domain.entity.dto.ChatMsgRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription: 消息回复事件
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Getter
public class MessageRecallEvent extends ApplicationEvent {

    private final ChatMsgRecallDTO recallDTO;

    public MessageRecallEvent(Object source, ChatMsgRecallDTO recallDTO) {
        super(source);
        this.recallDTO = recallDTO;
    }

}
