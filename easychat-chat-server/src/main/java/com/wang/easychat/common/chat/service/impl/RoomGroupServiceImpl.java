package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.mapper.RoomGroupMapper;
import com.wang.easychat.common.chat.service.IRoomGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class RoomGroupServiceImpl extends ServiceImpl<RoomGroupMapper, RoomGroup> implements IRoomGroupService {

    @Override
    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }

    /**
     * 通过roomId批量查询
     *
     * @param roomIds
     * @return
     */
    @Override
    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }
}
