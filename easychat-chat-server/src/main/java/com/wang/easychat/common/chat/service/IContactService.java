package com.wang.easychat.common.chat.service;

import com.wang.easychat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

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
}
