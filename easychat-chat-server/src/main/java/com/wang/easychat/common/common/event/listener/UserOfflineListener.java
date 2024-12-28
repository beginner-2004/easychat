package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.common.event.UserOfflineEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.adapter.WSAdapter;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/17
 **/
@Component
@Slf4j
public class UserOfflineListener {

    @Autowired
    private UserCache userCache;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private WSAdapter wsAdapter;
    @Autowired
    private IUserService userService;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        userCache.offline(user.getId(), user.getLastOptTime());
        // 推送给在在线用户
        webSocketService.sendToAllOnline(wsAdapter.buildOfflineNotifyResp(event.getUser()), event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveDB(UserOfflineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userService.updateById(update);
    }

}
