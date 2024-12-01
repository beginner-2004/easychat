package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.service.IRoomService;
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
 * @Date: 2024/11/29
 **/
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    @Autowired
    private IRoomService roomService;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomService.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }
}
