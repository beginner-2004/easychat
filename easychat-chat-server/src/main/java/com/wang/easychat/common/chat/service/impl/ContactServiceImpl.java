package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.Contact;
import com.wang.easychat.common.chat.mapper.ContactMapper;
import com.wang.easychat.common.chat.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
}
