package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.Room;
import com.wang.easychat.common.chat.mapper.RoomMapper;
import com.wang.easychat.common.chat.service.IRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-25
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {

    /**
     * 发送消息后更新房间信息
     *
     * @param roomId
     * @param msgId
     * @param msgTime
     */
    @Override
    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                // todo 加一层msg的 lt 判断，防止先消费后来的消息
                .eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }
}
