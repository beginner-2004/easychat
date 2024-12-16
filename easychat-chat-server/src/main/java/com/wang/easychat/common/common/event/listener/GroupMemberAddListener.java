package com.wang.easychat.common.common.event.listener;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.chat.service.adapter.MemberAdapter;
import com.wang.easychat.common.chat.service.adapter.RoomAdapter;
import com.wang.easychat.common.chat.service.cache.GroupMemberCache;
import com.wang.easychat.common.common.event.GroupMemberAddEvent;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.cache.UserInfoCache;
import com.wang.easychat.common.user.service.impl.PushService;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/13
 **/
@Component
@Slf4j
public class GroupMemberAddListener {

    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private ChatService chatService;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private IUserService userService;
    @Autowired
    private PushService pushService;


    /**
     * 给每个人发送建群信息
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event){
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = userInfoCache.get(inviteUid);
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }

    /**
     * 给前端返回消息体
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = userService.listByIds(uidList);
        users.forEach(user -> {
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(ws, memberUidList);
        });
        //移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }
}
