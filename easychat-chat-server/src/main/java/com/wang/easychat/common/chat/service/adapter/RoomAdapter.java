package com.wang.easychat.common.chat.service.adapter;

import com.wang.easychat.common.chat.domain.entity.Contact;
import com.wang.easychat.common.chat.domain.entity.GroupMember;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.enums.GroupRoleEnum;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.wang.easychat.common.user.domain.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
public class RoomAdapter {
    public static List<ChatMessageReadResp> buildReadResp(List<Contact> list) {
        return list.stream().map(contact -> {
            ChatMessageReadResp resp = new ChatMessageReadResp();
            resp.setUid(contact.getUid());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<GroupMember> buildGroupMemberBatch(List<Long> uidList, Long groupId) {
        return uidList.stream()
                .distinct()
                .map(uid -> {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setRole(GroupRoleEnum.MEMBER.getType());
                    groupMember.setUid(uid);
                    groupMember.setGroupId(groupId);
                    return groupMember;
                }).collect(Collectors.toList());
    }

    public static ChatMessageReq buildGroupAddMessage(RoomGroup groupRoom, User inviter, Map<Long, User> member) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(groupRoom.getRoomId());
        chatMessageReq.setMsgType(MessageTypeEnum.SYSTEM.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(inviter.getName())
                .append("\"")
                .append("邀请")
                .append(member.values().stream().map(u -> "\"" + u.getName() + "\"").collect(Collectors.joining(",")))
                .append("加入群聊");
        chatMessageReq.setBody(sb.toString());
        return chatMessageReq;
    }
}
