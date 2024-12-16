package com.wang.easychat.common.common.event;

import com.wang.easychat.common.user.domain.dto.FriendApplyDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/12
 **/
@Getter
public class FriendApplyEvent extends ApplicationEvent {
    private final FriendApplyDTO dto;

    public FriendApplyEvent(Object source, FriendApplyDTO dto) {
        super(source);
        this.dto = dto;
    }
}
