package com.wang.easychat.common.chat.service.cache;

import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/1
 **/
@Component
public class MsgCache {

    @Autowired
    private IMessageService messageService;

    @Cacheable(cacheNames = "msg", key = "'msg' + #msgId")
    public Message getMsg(Long msgId){
        return messageService.getById(msgId);
    }

    @CacheEvict(cacheNames = "msg", key = "'msg'+#msgId")
    public Message evictMsg(Long msgId) {
        return null;
    }
}
