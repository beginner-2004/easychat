package com.wang.easychat.common.common.event;

import com.wang.easychat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Getter
public class UserOnLineEvent extends ApplicationEvent {
    private User user;

    public UserOnLineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
