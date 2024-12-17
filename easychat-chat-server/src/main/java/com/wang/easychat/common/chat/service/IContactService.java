package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.dto.MsgReadInfoDTO;
import com.wang.easychat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReadReq;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会话列表 服务类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
public interface IContactService extends IService<Contact> {

    /**
     * 更新会话时间
     */
    void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date refreshTime);

    /**
     * 通过uid和会话id获取会话记录
     * @param id
     * @param receiveUid
     * @return
     */
    Contact getByRoomIdAndUid(Long id, Long receiveUid);

    /**
     * 查看已读列表
     * @param msg
     * @param request
     * @return
     */
    CursorPageBaseResp<Contact> getReadPage(Message msg, ChatMessageReadReq request);

    /**
     * 查看未读列表
     * @param msg
     * @param request
     * @return
     */
    CursorPageBaseResp<Contact> getUnReadPage(Message msg, ChatMessageReadReq request);

    /**
     * 获取会话信息
     * @param uid
     * @param roomId
     * @return
     */
    Contact get(Long uid, Long roomId);

    /**
     * 获取对应消息已读未读具体信息
     * @param messages
     * @return
     */
    Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages);

    /**
     * 获取用户基础会话
     * @param uid
     * @param request
     * @return
     */
    CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq request);

    /**
     * 通过房间id集合和用户id查找会话
     * @param roomIds
     * @param uid
     * @return
     */
    List<Contact> getByRoomIdsAndUid(List<Long> roomIds, Long uid);

    /**
     * 删除会话
     * @param roomId
     * @param uidList
     */
    Boolean removeByRoomId(Long roomId, List<Long> uidList);
}
