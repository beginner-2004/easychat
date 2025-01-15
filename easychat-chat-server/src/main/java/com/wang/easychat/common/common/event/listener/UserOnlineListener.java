package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.common.event.UserOnLineEvent;
import com.wang.easychat.common.common.event.UserRegisterEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.domain.enums.IdemporentEnum;
import com.wang.easychat.common.user.domain.enums.ItemEnum;
import com.wang.easychat.common.user.domain.enums.UserActiveStatusEnum;
import com.wang.easychat.common.user.service.IUserBackpackService;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.IpService;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.user.service.impl.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Date;

/**
 * @ClassDescription: 用户注册事件监听器
 * @Author:Wangzd
 * @Date: 2024/11/18
 **/
@Component
public class UserOnlineListener {
    @Autowired
    private IUserBackpackService userBackpackService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IpService ipService;
    @Autowired
    private UserCache userCache;
    @Autowired
    private PushService pushService;
    @Autowired
    private WSAdapter wsAdapter;

    @Async
    @EventListener(classes = UserOnLineEvent.class)
    public void saveRedisAndPush(UserOnLineEvent event) {
        User user = event.getUser();
        userCache.online(user.getId(), user.getLastOptTime());
        //推送给所有在线用户，该用户登录成功
        pushService.sendPushMsg(wsAdapter.buildOnlineNotifyResp(event.getUser()));
    }

    @Async
    @TransactionalEventListener(classes = UserOnLineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void saveDB(UserOnLineEvent event){
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userService.updateById(update);
        //更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }

}
