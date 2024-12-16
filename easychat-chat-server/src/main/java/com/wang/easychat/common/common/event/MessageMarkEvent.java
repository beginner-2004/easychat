package com.wang.easychat.common.common.event;

import com.wang.easychat.common.chat.domain.dto.ChatMessageMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Getter
public class MessageMarkEvent extends ApplicationEvent {

    private final ChatMessageMarkDTO dto;

    public MessageMarkEvent(Object source, ChatMessageMarkDTO dto) {
        super(source);
        this.dto = dto;
    }
}
