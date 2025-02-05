package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.common.event.UserBlackEvent;
import com.wang.easychat.common.common.event.UserRegisterEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.websocket.service.WebSocketService;
import com.wang.easychat.common.websocket.service.adapter.WebSocektAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.channels.Channel;

/**
 * @ClassDescription: 用户注册事件监听器
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Component
public class UserBlackListener {

    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserCache userCache;

    @Async
    @EventListener(classes = UserBlackEvent.class)
    public void refreshRedis(UserBlackEvent event) {
        userCache.evictBlackMap();
        userCache.remove(event.getUser().getId());
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event){
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocektAdapter.buildBlack(user));
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent event){
        userService.invalidUid(event.getUser().getId());
    }


}
