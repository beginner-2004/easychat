package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wang.easychat.common.chat.domain.dto.RoomBaseInfo;
import com.wang.easychat.common.chat.domain.entity.*;
import com.wang.easychat.common.chat.domain.enums.GroupRoleAPPEnum;
import com.wang.easychat.common.chat.domain.enums.GroupRoleEnum;
import com.wang.easychat.common.chat.domain.enums.HotFlagEnum;
import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.*;
import com.wang.easychat.common.chat.domain.vo.resp.ChatRoomResp;
import com.wang.easychat.common.chat.domain.vo.resp.MemberResp;
import com.wang.easychat.common.chat.service.*;
import com.wang.easychat.common.chat.service.adapter.ChatAdapter;
import com.wang.easychat.common.chat.service.adapter.MemberAdapter;
import com.wang.easychat.common.chat.service.adapter.RoomAdapter;
import com.wang.easychat.common.chat.service.cache.*;
import com.wang.easychat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.wang.easychat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.wang.easychat.common.common.annotation.RedissonLock;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.event.GroupMemberAddEvent;
import com.wang.easychat.common.common.exception.GroupErrorEnum;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.entity.Role;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.domain.enums.RoleEnum;
import com.wang.easychat.common.user.service.IRoleService;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.cache.UserCache;
import com.wang.easychat.common.user.service.cache.UserInfoCache;
import com.wang.easychat.common.user.service.impl.PushService;
import com.wang.easychat.common.websocket.domain.vo.resp.ChatMemberResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/10
 **/
@Service
@Slf4j
public class RoomAppServiceImpl implements IRoomAppService {

    @Autowired
    private IContactService contactService;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private RoomFriendCache roomFriendCache;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IRoomFriendService roomFriendService;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private UserCache userCache;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private IGroupMemberService groupMemberService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private PushService pushService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 游标翻页方式，获取会话列表
     */
    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        // 查出要展示的会话列表
        CursorPageBaseResp<Long> page;
        if (Objects.nonNull(uid)){
            Double hotEnd = getCursorOrNull(request.getCursor());
            Double hotStart = null;

            // 用户基础会话
            CursorPageBaseResp<Contact> contactPage = contactService.getContactPage(uid, request);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if (!contactPage.getIsLast()){
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
            // 热门房间
            Set<ZSetOperations.TypedTuple<String>> typedTuples =  hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::parseLong).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
            // 合并基础会话和热门房间
            page = CursorPageBaseResp.init(contactPage, baseRoomIds);
        }else { // 未登录，只查询全员群
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
        // 组装会话信息
        List<ChatRoomResp> result = buildContactResp(uid, page.getList());
        return CursorPageBaseResp.init(page, result);
    }

    /**
     * 查找会话信息
     *
     * @param uid
     * @param friendUid
     */
    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {
        String roomKey = ChatAdapter.generateRoomKey(Arrays.asList(uid, friendUid));
        RoomFriend roomFriend = roomFriendService.getRoomFriend(roomKey);
        AssertUtil.isNotEmpty(roomFriend, "他不是您的好友！");
        return buildContactResp(uid, Collections.singletonList(roomFriend.getRoomId())).get(0);
    }

    /**
     * 返回会话消息信息
     */
    @Override
    public ChatRoomResp getContactDetail(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        return buildContactResp(uid, Collections.singletonList(roomId)).get(0);
    }

    /**
     * 创建群组
     */
    @Override
    @Transactional
    public Long addGroup(Long uid, GroupAddReq request) {
        RoomGroup roomGroup = roomService.createGroupRoom(uid);
        // 批量保存群成员
        List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(request.getUidList(), roomGroup.getId());
        groupMemberService.saveBatch(groupMembers);

        // 发送创建群聊消息给每一个用户
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));

        return roomGroup.getRoomId();
    }

    /**
     * 获取群组信息
     * @param uid
     * @param roomId
     * @return
     */
    @Override
    public MemberResp getGroupDetail(Long uid, long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(roomGroup, "roomId有误");
        Long onlineNum;
        if (isHotGroup(room)){
            onlineNum = userCache.getOnlineNum();
        }else {
            List<Long> memberUidList = groupMemberService.getMemberUidList(roomGroup.getId());
            onlineNum = userService.getOnlineCount(memberUidList).longValue();
        }
        GroupRoleAPPEnum groupRole = getGroupRole(uid, roomGroup, room);
        return MemberResp.builder()
                .avatar(roomGroup.getAvatar())
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .onlineNum(onlineNum)
                .role(groupRole.getType())
                .build();
    }

    /**
     * 邀请好友
     * @param uid
     * @param request
     */
    @Override
    @RedissonLock(key = "#request.roomId")
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long uid, MemberAddReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误！");
        AssertUtil.isFalse(isHotGroup(room), "全员群不需要邀请好友");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, " 房间号有误");
        GroupMember self = groupMemberService.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, "您不是群成员");
        List<Long> memberBatch = groupMemberService.getMemberUidList(roomGroup.getId(), request.getUidList());
        Set<Long> existUid = new HashSet<>(memberBatch);
        List<Long> needAddUidList = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(needAddUidList)){
            return;
        }
        List<GroupMember> groupMembers = MemberAdapter.buildMemberAdd(roomGroup.getId(), needAddUidList);
        groupMemberService.saveBatch(groupMembers);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));

    }

    /**
     * @param request
     * @return
     */
    @Override
    @Cacheable(cacheNames = "member", key = "'memberList.' + #request.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (isHotGroup(room)){  // 全员群只展示100名用户
            List<User> memberList = userService.getMemberList();
            return MemberAdapter.buildMemberList(memberList);
        }else {
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            List<Long> memberUidList = groupMemberService.getMemberUidList(roomGroup.getId());
            Map<Long, User> batch = userInfoCache.getBatch(memberUidList);
            return MemberAdapter.buildMemberList(batch);
        }
    }

    /**
     * 查询成员列表
     *
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        List<Long> memberUidList;
        if (isHotGroup(room)){  // 全员群展示所有用户
            memberUidList = null;
        }else { // 只展示房间内的群成员
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            memberUidList = groupMemberService.getMemberUidList(roomGroup.getId());
        }
        return chatService.getMemberPage(memberUidList, request);
    }

    /**
     * 移除群成员
     *
     * @param uid
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMember(Long uid, MemberDelReq request) {
        // 判断房间是否有误
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");
        // 判断执行操作者是否在群中
        GroupMember self = groupMemberService.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP);
        // 判断被移除的人是否是 管理员 或者 群主，群主不能被移除，管理员只能被群主移除
        Long removedUid = request.getUid();
        // 1.1.如果是群主则为非法操作
        AssertUtil.isFalse(groupMemberService.isLord(roomGroup.getId(), removedUid), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        // 1.2.如果是管理员，判断是否是群主操作
        if (groupMemberService.isManager(roomGroup.getId(), removedUid)){
            Boolean isLord = groupMemberService.isLord(roomGroup.getId(), uid);
            AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
        // 1.3.如果是普通成员，判断是否有权限操作
        AssertUtil.isTrue(hasPower(self), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberService.getMember(roomGroup.getId(), removedUid);
        AssertUtil.isNotEmpty(member, "用户已经移除");
        groupMemberService.removeById(member.getId());
        // 发送移除事件告知群成员
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
        pushService.sendPushMsg(ws, memberUidList);
        groupMemberCache.evictMemberUidList(room.getId());
    }

    /**
     * 判断是否有权限
     * @param self
     * @return
     */
    private boolean hasPower(GroupMember self) {
        return Objects.equals(self.getRole(), GroupRoleEnum.LEADER.getType())
                || Objects.equals(self.getRole(), GroupRoleEnum.MEMBER.getType())
                || roleService.hasPower(self.getUid(), RoleEnum.ADMIN);
    }

    private GroupRoleAPPEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        GroupMember member = Objects.isNull(uid) ? null : groupMemberService.getMember(roomGroup.getId(), uid);
        if (Objects.nonNull(member)){
            return GroupRoleAPPEnum.of(member.getRole());
        } else if (isHotGroup(room)) {
            return GroupRoleAPPEnum.MEMBER;
        } else {
            return GroupRoleAPPEnum.REMOVE;
        }
    }

    /**
     * 判断是否是热门房间
     * @param room
     * @return
     */
    private boolean isHotGroup(Room room) {
        return HotFlagEnum.YES.getType().equals(room.getHotFlag());
    }


    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        // 会话名称和头像
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
        // 最后一条信息
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() : messageService.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 消息未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream().map(room -> {
            ChatRoomResp resp = new ChatRoomResp();
            RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
            BeanUtils.copyProperties(roomBaseInfo, resp);
            resp.setHot_Flag(roomBaseInfo.getHotFlag());
            Message message = msgMap.get(room.getLastMsgId());
            if (Objects.nonNull(message)){
                AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                resp.setText(lastMsgUidMap.get(message.getFromUid()).getName() + ":" + strategyNoNull.showContactMsg(message));
            }
            resp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
            return resp;
        })
                .sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        List<Contact> contacts = contactService.getByRoomIdsAndUid(roomIds, uid);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageService.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }


    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 根据好友和群组类型分组
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群组信息
        List<Long> groupRoomIds = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> roomInfoBatch = roomGroupCache.getBatch(groupRoomIds);
        // 获取好友信息
        List<Long> friendRoomIds = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomIds, uid);

        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            BeanUtils.copyProperties(room, roomBaseInfo);
            roomBaseInfo.setRoomId(room.getId());
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP){
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND){
                User user = friendRoomMap.get(room.getId());
                roomBaseInfo.setName(user.getName());
                roomBaseInfo.setAvatar(user.getAvatar());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(roomIds);
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));

    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }
}
