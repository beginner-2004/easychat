package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;
import java.util.List;

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

    /**
     * 获取游标翻页的消息体
     * @param roomId
     * @param request
     * @param lastMsgId
     * @return
     */
    CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request, Long lastMsgId);

    /**
     * 获取房间未读消息条数
     * @param roomId
     * @param readTime
     * @return
     */
    Integer getUnReadCount(Long roomId, Date readTime);

    /**
     * 根据roomId,uid集合删除消息记录
     */
    Boolean removeByRoomId(Long roomId, List uidList);
}
