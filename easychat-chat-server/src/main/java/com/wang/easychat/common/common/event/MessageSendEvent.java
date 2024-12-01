package com.wang.easychat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription: 消息事件
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
