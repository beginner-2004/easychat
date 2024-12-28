package com.wang.easychat.common.common.event;

import com.wang.easychat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/17
 **/
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private final User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}