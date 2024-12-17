package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import com.wang.easychat.common.chat.domain.dto.MsgReadInfoDTO;
import com.wang.easychat.common.chat.domain.entity.*;
import com.wang.easychat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.*;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.*;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.chat.service.adapter.RoomAdapter;
import com.wang.easychat.common.chat.service.cache.RoomCache;
import com.wang.easychat.common.chat.service.cache.RoomGroupCache;
import com.wang.easychat.common.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.wang.easychat.common.chat.service.strategy.mark.MsgMarkFactory;
import com.wang.easychat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.wang.easychat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.wang.easychat.common.chat.service.strategy.msg.RecallMsgHandler;
import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.event.MessageSendEvent;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.service.IRoleService;
import com.wang.easychat.common.websocket.domain.vo.resp.ChatMemberResp;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    /**
     * 标记消息
     *
     * @param uid
     * @param request
     */
    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())){
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }
    }

    /**
     * 查询消息已读情况
     */
    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadPage(@Nullable Long uid, ChatMessageReadReq request) {
        Message msg = messageService.getById(request.getMsgId());
        AssertUtil.isNotEmpty(msg, "消息id有误");
        AssertUtil.equal(uid, msg.getFromUid(), "只能查看自己的消息");
        CursorPageBaseResp<Contact> page;
        if (request.getSearchType() == 1){
            // 查询已读
            page = contactService.getReadPage(msg, request);
        }else {
            // 查询未读
            page = contactService.getUnReadPage(msg, request);
        }
        if (CollectionUtil.isEmpty(page.getList())) {
            return CursorPageBaseResp.empty();
        }

        return CursorPageBaseResp.init(page, RoomAdapter.buildReadResp(page.getList()));
    }

    /**
     * 读取消息
     */
    @Override
    @RedissonLock(key = "#uid")
    public void msgRead(Long uid, ChatMessageMemberReq request) {
        Contact contact = contactService.get(uid, request.getRoomId());
        if (Objects.nonNull(contact)) {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactService.updateById(update);
        } else {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(request.getRoomId());
            insert.setReadTime(new Date());
            contactService.save(insert);
        }
    }

    @Override
    public Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request) {
        List<Message> messages = messageService.listByIds(request.getMsgIds());
        messages.forEach(msg -> {
            AssertUtil.equal(uid, msg.getFromUid(), "只能查询自己发送的消息");
        });
        return contactService.getMsgReadInfo(messages).values();
    }

    /**
     * 获取群成员列表
     *
     * @param memberUidList
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request) {
        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor());
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
        if (Objects.equals(uid, User.UID_SYSTEM)){
            // 系统消息跳过检验
            return;
        }
        if (room.isHotRoom()){
            // 全员群跳过检验
            return;
        }
        if (room.isRoomFriend()){
            RoomFriend roomFriend = roomFriendService.getByRoomId(request.getRoomId());
            AssertUtil.equal(NormalOrNoEnum.NORMAL.getStatus(), roomFriend.getStatus(), "请先添加好友！");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被对方拉黑");
        }
        if (room.isRoomGroup()){
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            GroupMember groupMember = groupMemberService.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(groupMember, "您已不是该群成员");
        }
    }
}
