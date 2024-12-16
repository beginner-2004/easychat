package com.wang.easychat.common.chat.service.adapter;

import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.enums.HotFlagEnum;
import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.user.domain.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassDescription: 聊天适配器
 * @Author:Wangzd
 * @Date: 2024/11/25
 **/
public class ChatAdapter {
    public static final String SEPARATOR = ",";

    public static String generateRoomKey(List<Long> uidList) {
        return uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }

    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;
    }

    /**
     * 获取朋友的uid集合
     */
    public static Set<Long> getFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream()
                .map(a -> getFriendUid(a, uid))
                .collect(Collectors.toSet());
    }

    /**
     * 获取好友uid
     */
    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {
        return Objects.equals(roomFriend.getUid1(), uid) ? roomFriend.getUid2() : roomFriend.getUid1();
    }

    public static RoomGroup buildGroupRoom(User user, Long roomId) {
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setName(user.getName() + "的群聊");
        roomGroup.setAvatar(user.getAvatar());
        roomGroup.setRoomId(roomId);
        return roomGroup;
    }
}
