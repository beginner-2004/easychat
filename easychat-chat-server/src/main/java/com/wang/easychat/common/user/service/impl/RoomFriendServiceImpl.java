package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.chat.domain.enums.RoomTypeEnum;
import com.wang.easychat.common.chat.service.ChatAdapter;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.user.domain.entity.Room;
import com.wang.easychat.common.user.domain.entity.RoomFriend;
import com.wang.easychat.common.user.mapper.RoomFriendMapper;
import com.wang.easychat.common.user.service.IRoomFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.user.service.IRoomService;
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
        AssertUtil.equal(uidList, 2, "房间创建失败，好友人数有误");
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
        return null;
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
