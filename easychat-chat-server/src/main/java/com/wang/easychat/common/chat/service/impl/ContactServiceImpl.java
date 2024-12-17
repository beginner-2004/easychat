package com.wang.easychat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wang.easychat.common.chat.domain.dto.MsgReadInfoDTO;
import com.wang.easychat.common.chat.domain.entity.Contact;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReadReq;
import com.wang.easychat.common.chat.mapper.ContactMapper;
import com.wang.easychat.common.chat.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.common.domain.vo.req.CursorPageBaseReq;
import com.wang.easychat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.wang.easychat.common.common.utils.AssertUtil;
import com.wang.easychat.common.common.utils.CursorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements IContactService {
    @Autowired
    private ContactMapper contactMapper;

    /**
     * 更新会话时间
     */
    @Override
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date refreshTime) {
        contactMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, refreshTime);
    }

    /**
     * 通过uid和会话id获取会话记录
     *
     * @param roomId
     * @param receiveUid
     * @return
     */
    @Override
    public Contact getByRoomIdAndUid(Long roomId, Long receiveUid) {
        return lambdaQuery()
                .eq(Contact::getUid, receiveUid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    /**
     * 查看已读列表
     *
     * @param msg
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<Contact> getReadPage(Message msg, ChatMessageReadReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Contact::getRoomId, msg.getRoomId());
            wrapper.ne(Contact::getUid, msg.getFromUid());  // 不需要查询出自己
            wrapper.ge(Contact::getReadTime, msg.getCreateTime());  // 已读时间大于消息发送时间
        }, Contact::getReadTime);
    }

    /**
     * 查看未读列表
     *
     * @param msg
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<Contact> getUnReadPage(Message msg, ChatMessageReadReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Contact::getRoomId, msg.getRoomId());
            wrapper.ne(Contact::getUid, msg.getFromUid());  // 不需要查询出自己
            wrapper.lt(Contact::getReadTime, msg.getCreateTime());  // 未读时间小于消息发送时间
        }, Contact::getReadTime);
    }

    /**
     * 获取会话信息
     *
     * @param uid
     * @param roomId
     * @return
     */
    @Override
    public Contact get(Long uid, Long roomId) {
        return lambdaQuery()
                .eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    /**
     * 获取对应消息已读未读具体信息
     *
     * @param messages
     * @return
     */
    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(), 1, "只能查相同房间下的消息");
        Long roomId = roomGroup.keySet().iterator().next();
        Integer totalCount = getTotalCount(roomId);
        return messages.stream().map(msg -> {
            MsgReadInfoDTO readInfoDTO = new MsgReadInfoDTO();
            readInfoDTO.setMsgId(msg.getId());
            Integer readCount = getReadCount(msg);
            readInfoDTO.setReadCount(readCount);
            readInfoDTO.setUnReadCount(totalCount - readCount - 1);
            return readInfoDTO;
        }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId, Function.identity()));
    }

    /**
     * 获取用户基础会话
     *
     * @param uid
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Contact::getUid, uid);
        }, Contact::getActiveTime);
    }

    /**
     * 通过房间id集合和用户id查找会话
     *
     * @param roomIds
     * @param uid
     * @return
     */
    @Override
    public List<Contact> getByRoomIdsAndUid(List<Long> roomIds, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId, roomIds)
                .eq(Contact::getUid, uid)
                .list();
    }

    /**
     * 删除会话
     */
    @Override
    public Boolean removeByRoomId(Long roomId, List<Long> uidList) {
        LambdaQueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().lambda()
                .eq(Contact::getRoomId, roomId)
                .in(CollectionUtil.isNotEmpty(uidList), Contact::getUid, uidList);
        return remove(wrapper);
    }

    private Integer getReadCount(Message msg) {
        return lambdaQuery()
                .eq(Contact::getRoomId, msg.getRoomId())
                .ne(Contact::getUid, msg.getFromUid())
                .ge(Contact::getReadTime, msg.getCreateTime())
                .count();
    }

    private Integer getTotalCount(Long roomId) {
        return lambdaQuery()
                .eq(Contact::getRoomId, roomId)
                .count();
    }
}
