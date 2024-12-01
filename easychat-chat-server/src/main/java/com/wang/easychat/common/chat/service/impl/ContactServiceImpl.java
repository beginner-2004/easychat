package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.Contact;
import com.wang.easychat.common.chat.mapper.ContactMapper;
import com.wang.easychat.common.chat.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
