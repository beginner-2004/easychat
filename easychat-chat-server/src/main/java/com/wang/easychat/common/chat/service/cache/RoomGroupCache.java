package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.service.IRoomGroupService;
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
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {
    @Autowired
    private IRoomGroupService roomGroupService;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, roomId);
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroups = roomGroupService.listByIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomGroup::getId, Function.identity()));
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }
}
