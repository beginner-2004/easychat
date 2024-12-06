package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.wang.easychat.common.chat.domain.entity.*;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.*;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.chat.service.cache.RoomCache;
import com.wang.easychat.common.chat.service.cache.RoomGroupCache;
import com.wang.easychat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.wang.easychat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.wang.easychat.common.chat.service.strategy.msg.RecallMsgHandler;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.event.MessageSendEvent;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private IContactService contactService;
    @Autowired
    private RecallMsgHandler recallMsgHandler;
    @Autowired
    private IRoleService roleService;

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        // 检查用户是否有权限发送消息
        check(request, uid);
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
        // 消息发送事件
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

    /**
     * 获取会话消息
     */
    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long receiveUid) {
        // 通过最后一条消息限制被移除群聊的人能读取的消息
        Long lastMsgId = getLastMsgId(request.getRoomId(), receiveUid);
        CursorPageBaseResp<Message> cursorPage = messageService.getCursorPage(request.getRoomId(), request, lastMsgId);
        if (cursorPage.isEmpty()){
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }

    /**
     * 撤回消息
     *
     * @param uid
     * @param request
     */
    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message msg = messageService.getById(request.getMsgId());
        // 校验是否能撤回
        checkRecall(uid, msg);
        // 执行消息撤回
        recallMsgHandler.recall(uid, msg);
    }

    private void checkRecall(Long uid, Message msg) {
        AssertUtil.isNotEmpty(msg, "消息有误");
        AssertUtil.notEqual(msg.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
        boolean hasPower = roleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower){
            return;
        }
        AssertUtil.equal(uid, msg.getFromUid(), "抱歉，您没有权限撤回！");
        long between = DateUtil.between(msg.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "发送超过两分钟消息无法撤回！");

    }

    /**
     * 获取最后一条可见消息的id
     * @param roomId
     * @param receiveUid
     * @return
     */
    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (room.isHotRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid, "请先登录");
        Contact contact = contactService.getByRoomIdAndUid(room.getId(), receiveUid);
        return contact.getLastMsgId();
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
