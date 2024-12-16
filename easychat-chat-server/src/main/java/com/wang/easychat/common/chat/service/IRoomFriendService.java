package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
public interface IRoomFriendService extends IService<RoomFriend> {

    /**
     * 创建朋友房间
     * @param asList
     * @return
     */
    RoomFriend createFriendRoom(List<Long> asList);

    /**
     * 禁用房间
     * @param asList
     */
    void disableFriendRoom(List<Long> asList);

    /**
     * 根据roomId查询房间信息
     */
    RoomFriend getByRoomId(Long roomId);

    /**
     * 通过roomIds查找
     * @param roomIds
     * @return
     */
    List<RoomFriend> listByRoomIds(List<Long> roomIds);

    /**
     * 通过roomKey查找房间
     * @param roomKey
     * @return
     */
    RoomFriend getRoomFriend(String roomKey);
}
