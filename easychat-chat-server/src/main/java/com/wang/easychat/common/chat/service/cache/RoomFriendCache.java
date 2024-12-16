package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.chat.service.IRoomFriendService;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/10
 **/
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {

    @Autowired
    private IRoomFriendService roomFriendService;

    @Override
    protected String getKey(Long groupId) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, groupId);
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomGroups = roomFriendService.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }
}
