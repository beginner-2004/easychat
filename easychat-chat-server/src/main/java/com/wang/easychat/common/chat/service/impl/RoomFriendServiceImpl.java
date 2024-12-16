package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.chat.service.adapter.ChatAdapter;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.chat.mapper.RoomFriendMapper;
import com.wang.easychat.common.chat.service.IRoomFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.chat.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
public class RoomFriendServiceImpl extends ServiceImpl<RoomFriendMapper, RoomFriend> implements IRoomFriendService {
    @Autowired
    private IRoomService roomService;

    /**
     * 创建朋友房间
     * @param uidList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友人数有误");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友人数有误");
        String key = ChatAdapter.generateRoomKey(uidList);
        RoomFriend roomFriend = getByKey(key);
        if (Objects.nonNull(roomFriend)){
            // 存在房间就恢复
            restoreRoomIfNeed(roomFriend);
        }else {
            // 否则新建房间
            Room room = createRoom(RoomTypeEnum.FRIEND);
            // 创建 roomFriend 关系
            roomFriend = createFriendRoom(room.getId(), uidList);
        }
        return roomFriend;
    }

    /**
     * 禁用房间
     * @param uidList
     */
    @Override
    public void disableFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);
        disableRoom(key);
    }

    /**
     * 根据roomId查询房间信息
     *
     * @param roomId
     */
    @Override
    public RoomFriend getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .one();
    }

    /**
     * 通过roomIds查找
     *
     * @param roomIds
     * @return
     */
    @Override
    public List<RoomFriend> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomFriend::getRoomId, roomIds)
                .list();
    }

    /**
     * 通过roomKey查找房间
     *
     * @param roomKey
     * @return
     */
    @Override
    public RoomFriend getRoomFriend(String roomKey) {
        return getByKey(roomKey);
    }

    private void disableRoom(String key) {
        lambdaUpdate()
                .eq(RoomFriend::getRoomKey, key)
                .set(RoomFriend::getStatus, NormalOrNoEnum.NOT_NORMAL.getStatus())
                .update();
    }

    private RoomFriend createFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend insert = ChatAdapter.buildFriendRoom(roomId, uidList);
        save(insert);
        return insert;
    }

    /**
     * 创建房间
     * @param typeEnum
     * @return
     */
    private Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = ChatAdapter.buildRoom(typeEnum);
        roomService.save(insert);
        return insert;
    }

    /**
     * 恢复好友房间
     * @param roomFriend
     */
    private void restoreRoomIfNeed(RoomFriend roomFriend) {
        if (Objects.equals(roomFriend.getStatus(), NormalOrNoEnum.NOT_NORMAL.getStatus())) {
            lambdaUpdate()
                    .eq(RoomFriend::getId, roomFriend.getId())
                    .set(RoomFriend::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                    .update();
        }
    }

    /**
     * 根据 roomkey 查找房间
     * @param key
     * @return
     */
    private RoomFriend getByKey(String key) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomKey, key)
                .one();
    }
}
