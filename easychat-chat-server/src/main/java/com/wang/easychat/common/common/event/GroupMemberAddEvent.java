package com.wang.easychat.common.common.event;

import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @ClassDescription: 创建群组事件
 * @Author:Wangzd
 * @Date: 2024/12/13
 **/
@Getter
public class GroupMemberAddEvent extends ApplicationEvent {

    private final List<GroupMember> memberList;
    private final RoomGroup roomGroup;
    private final Long inviteUid;

    public GroupMemberAddEvent(Object source, RoomGroup roomGroup, List<GroupMember> memberList, Long inviteUid) {
        super(source);
        this.memberList = memberList;
        this.roomGroup = roomGroup;
        this.inviteUid = inviteUid;
    }
}
