package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.enums.GroupRoleEnum;
import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.chat.mapper.RoomMapper;
import com.wang.easychat.common.chat.service.IGroupMemberService;
import com.wang.easychat.common.chat.service.IRoomFriendService;
import com.wang.easychat.common.chat.service.IRoomGroupService;
import com.wang.easychat.common.chat.service.IRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.chat.service.adapter.ChatAdapter;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private IRoomGroupService roomGroupService;
    @Autowired
    private IGroupMemberService groupMemberService;

    /**
     * 发送消息后更新房间信息
     *
     * @param roomId
     * @param msgId
     * @param msgTime
     */
    @Override
    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                // todo 加一层msg的 lt 判断，防止先消费后来的消息
                .eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }

    /**
     * 创建群聊房间
     *
     * @param uid
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomGroup createGroupRoom(Long uid) {
        User user = userInfoCache.get(uid);
        Room room = createRoom(RoomTypeEnum.GROUP);
        // 插入群
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user, room.getId());
        roomGroupService.save(roomGroup);
        // 插入群主
        GroupMember leader = GroupMember.builder()
                .role(GroupRoleEnum.LEADER.getType())
                .groupId(roomGroup.getId())
                .uid(uid)
                .build();
        groupMemberService.save(leader);
        return roomGroup;
    }

    /**
     * 创建房间
     */
    private Room createRoom(RoomTypeEnum type) {
        Room insert = ChatAdapter.buildRoom(type);
        save(insert);
        return insert;
    }
}
