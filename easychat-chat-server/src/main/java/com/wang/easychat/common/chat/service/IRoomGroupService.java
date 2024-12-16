package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 群聊房间表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface IRoomGroupService extends IService<RoomGroup> {

    RoomGroup getByRoomId(Long roomId);

    /**
     * 通过roomId批量查询
     * @param roomIds
     * @return
     */
    List<RoomGroup> listByRoomIds(List<Long> roomIds);
}
