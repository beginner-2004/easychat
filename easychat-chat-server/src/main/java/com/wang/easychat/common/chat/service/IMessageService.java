package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 消息表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface IMessageService extends IService<Message> {

    /**
     * 查看当前消息和回复的消息直接的距离
     * @param roomId
     * @param replyMsgId
     * @param id
     * @return
     */
    Integer getGapCount(Long roomId, Long replyMsgId, Long id);
}
