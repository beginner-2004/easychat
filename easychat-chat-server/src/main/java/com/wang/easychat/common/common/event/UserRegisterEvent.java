package com.wang.easychat.common.common.event;

import com.wang.easychat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassDescription: 用户注册事件
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Getter
public class UserRegisterEvent extends ApplicationEvent {

    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
