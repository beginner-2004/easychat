package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.enums.MessageStatusEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.wang.easychat.common.chat.mapper.MessageMapper;
import com.wang.easychat.common.chat.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
     * @param replyMsgId
     * @param msgId
     * @return
     */
    @Override
    public Integer getGapCount(Long roomId, Long replyMsgId, Long msgId) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, replyMsgId)
                .le(Message::getId, msgId)
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
    public CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        }, Message::getId);
    }

    /**
     * 获取房间未读消息条数
     *
     * @param roomId
     * @param readTime
     * @return
     */
    @Override
    public Integer getUnReadCount(Long roomId, Date readTime) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count();
    }

    /**
     * 根据roomId,uid集合删除消息记录
     */
    @Override
    public Boolean removeByRoomId(Long roomId, List uidList) {
        if (CollectionUtil.isNotEmpty(uidList)){
            LambdaUpdateWrapper<Message> wrapper = new UpdateWrapper<Message>().lambda()
                    .eq(Message::getRoomId, roomId)
                    .in(Message::getFromUid, uidList)
                    .set(Message::getStatus, MessageStatusEnum.DELETE.getStatus());
            return this.remove(wrapper);
        }
        return false;
    }


}
