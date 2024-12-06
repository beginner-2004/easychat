package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.enums.MessageStatusEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.wang.easychat.common.chat.mapper.MessageMapper;
import com.wang.easychat.common.chat.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    /**
     * 查看当前消息和回复的消息直接的距离
     *
     * @param roomId
     * @param fromId
     * @param toId
     * @return
     */
    @Override
    public Integer getGapCount(Long roomId, Long fromId, Long toId) {
        return lambdaQuery()
                .eq(Message::getFromUid, roomId)
                .gt(Message::getId, fromId)
                .le(Message::getId, toId)
                .count();
    }

    /**
     * 获取游标翻页的消息体
     * @param roomId
     * @param request
     * @param lastMsgId
     * @return
     */
    @Override
    public CursorPageBaseResp<Message> getCursorPage(Long roomId, ChatMessagePageReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        }, Message::getId);
    }
}
