package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.enums.GroupRoleEnum;
import com.wang.easychat.common.chat.domain.vo.req.MemberExitReq;
import com.wang.easychat.common.chat.mapper.GroupMemberMapper;
import com.wang.easychat.common.chat.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.chat.service.adapter.MemberAdapter;
import com.wang.easychat.common.chat.service.cache.GroupMemberCache;
import com.wang.easychat.common.common.event.DelGroupEvent;
import com.wang.easychat.common.common.exception.CommonErrorEnum;
import com.wang.easychat.common.common.exception.GroupErrorEnum;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.dto.GroupDelDTO;
import com.wang.easychat.common.user.service.impl.PushService;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSMemberChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements IGroupMemberService {

    @Autowired
    private IRoomGroupService roomGroupService;
    @Autowired
    @Lazy
    private IRoomService roomService;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private IContactService contactService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private PushService pushService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 查询群成员
     */
    @Override
    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }

    /**
     * 查询群组所有成员
     */
    @Override
    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 查询群组传入的成员
     * @param groupId
     * @param uidList
     */
    @Override
    public List<Long> getMemberUidList(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    @Override
    public GroupMember getByUid(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .one();
    }

    @Override
    public GroupMember getByUidAndGroupId(Long uid, Long groupId) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getGroupId, groupId)
                .one();
    }

    /**
     * 退出群聊
     * @param uid
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitGroup(Long uid, MemberExitReq request) {
        Long roomId = request.getRoomId();
        // 1.判断是否存在房间
        RoomGroup roomGroup = roomGroupService.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2.判断房间是否是全员群
        Room room = roomService.getById(roomId);
        AssertUtil.isFalse(room.isHotRoom(), GroupErrorEnum.NOT_ALLOWED_FOR_EXIT_GROUP);

        // 3.判断是否在群中
        Boolean isGroupShip = isGroupShip(roomGroup.getRoomId(), Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4.是否是群主
        Boolean isLord = isLord(roomGroup.getId(), uid);
        if (isLord) {
            // 4.1.删除房间
            // boolean isDelRoom = roomService.removeById(roomId);
            // AssertUtil.isTrue(isDelRoom, CommonErrorEnum.SYSTEM_ERROR);
            // 4.2.删除会话
            // Boolean isDelContact = contactService.removeByRoomId(roomId, Collections.emptyList());
            // AssertUtil.isTrue(isDelContact, CommonErrorEnum.SYSTEM_ERROR);

            // 4.3.删除群成员
            Boolean isDelGroupMember = removeByGroupId(roomGroup.getId(), Collections.emptyList());
            AssertUtil.isTrue(isDelGroupMember, CommonErrorEnum.SYSTEM_ERROR);

            // 4.4.删除消息记录
            // Boolean isDelMessage = messageService.removeByRoomId(roomId, Collections.EMPTY_LIST);
            // AssertUtil.isTrue(isDelMessage, CommonErrorEnum.SYSTEM_ERROR);

            applicationEventPublisher.publishEvent(new DelGroupEvent(this, new GroupDelDTO(roomId, roomGroup.getId())));
        }else {
            // 4.5.删除会话
            // Boolean isDelContact = contactService.removeByRoomId(roomId, Collections.singletonList(uid));
            // AssertUtil.isTrue(isDelContact, CommonErrorEnum.SYSTEM_ERROR);
            // 4.6 删除群成员
            Boolean isDelGroupMember = removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
            AssertUtil.isTrue(isDelGroupMember, CommonErrorEnum.SYSTEM_ERROR);
            // 4.7 发送移除事件告知群成员
            List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), uid);
            pushService.sendPushMsg(ws, memberUidList);
            groupMemberCache.evictMemberUidList(room.getId());
        }
    }

    /**
     * 根据id删除群成员
     */
    @Override
    public Boolean removeByGroupId(Long groupId, List<Object> uidList) {

        LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                .lambda()
                .eq(GroupMember::getGroupId, groupId)
                .in(CollectionUtil.isNotEmpty(uidList), GroupMember::getUid, uidList);
        return remove(wrapper);
    }


    /**
     * 判断是否是群主
     */
    private Boolean isLord(Long groupId, Long uid) {
        GroupMember groupMember = lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 判断是否在群中
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }
}
