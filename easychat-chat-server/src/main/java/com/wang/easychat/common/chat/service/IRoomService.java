package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
