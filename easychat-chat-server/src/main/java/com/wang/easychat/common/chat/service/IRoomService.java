package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.chat.domain.entity.RoomFriend;
import com.wang.easychat.common.chat.domain.entity.RoomGroup;
import com.wang.easychat.common.chat.domain.vo.resp.ChatRoomResp;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
public interface IRoomService extends IService<Room> {
    /**
     * 发送消息后更新房间信息
     */
    void refreshActiveTime(Long roomId, Long msgId, Date msgTime);

    /**
     * 创建群聊房间
     * @param uid
     * @return
     */
    RoomGroup createGroupRoom(Long uid);
}
