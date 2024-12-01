package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.wang.easychat.common.chat.domain.entity.*;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.*;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.chat.service.cache.RoomCache;
import com.wang.easychat.common.chat.service.cache.RoomGroupCache;
import com.wang.easychat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.wang.easychat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.event.MessageSendEvent;
import com.wang.easychat.common.common.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RoomCache roomCache;
    @Autowired
    private IRoomFriendService roomFriendService;
    @Autowired
    private IRoomGroupService roomGroupService;
    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private IGroupMemberService groupMemberService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IMessageMarkService messageMarkService;

    /**
     * 发送消息
     */
    @Override
    public Long sendMsg(ChatMessageReq request, Long uid) {
        // 检查用户是否有权限发送消息
        check(request, uid);
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);

        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));

        return msgId;
    }

    /**
     * 整合消息展示体给前端
     */
    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message msg = messageService.getById(msgId);
        return getMsgResp(msg, receiveUid);
    }

    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    private List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)){
            return new ArrayList<>();
        }
        // 查询消息标志
        List<MessageMark> msgMark = messageMarkService.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, msgMark, receiveUid);
    }

    /**
     * 检查用户是否有权限发送消息
     */
    private void check(ChatMessageReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        if (room.isHotRoom()){
            // 全员群跳过检验
            return;
        }
        if (room.isRoomFriend()){
            RoomFriend roomFriend = roomFriendService.getById(request.getRoomId());
            AssertUtil.equal(NormalOrNoEnum.NORMAL.getStatus(), roomFriend.getStatus(), "您已经被对方拉黑");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被对方拉黑");
        }
        if (room.isRoomGroup()){
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            GroupMember groupMember = groupMemberService.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(groupMember, "您已不是该群成员");
        }
    }
}
