package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.service.IGroupMemberService;
import com.wang.easychat.common.chat.service.IMessageService;
import com.wang.easychat.common.chat.service.IRoomGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @ClassDescription: 群成员缓存
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@Component
public class GroupMemberCache {
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IRoomGroupService roomGroupService;
    @Autowired
    private IGroupMemberService groupMemberService;


    @Cacheable(cacheNames = "member", key = "'groupMember' + #roomId")
    public List<Long> getMemberUidList(Long roomId){
        RoomGroup roomGroup = roomGroupService.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)){
            return null;
        }
        return groupMemberService.getMemberUidList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = "member", key = "'groupMember' + #roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }
}
