package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.common.event.UserRegisterEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @ClassDescription: 用户注册事件监听器
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Component
public class UserRegisterListener {
    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private IUserService userService;

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdemporentEnum.UID, user.getId().toString());
    }

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event){
        // 前100名注册徽章
        User user = event.getUser();
        int registeredCount = userService.count();
        if (registeredCount < 10){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdemporentEnum.UID, user.getId().toString());
        } else if (registeredCount < 100){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdemporentEnum.UID, user.getId().toString());
        }

    }
}
