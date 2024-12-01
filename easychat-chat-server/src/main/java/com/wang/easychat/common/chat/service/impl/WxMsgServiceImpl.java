package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.WxMsg;
import com.wang.easychat.common.chat.mapper.WxMsgMapper;
import com.wang.easychat.common.chat.service.IWxMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信消息表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class WxMsgServiceImpl extends ServiceImpl<WxMsgMapper, WxMsg> implements IWxMsgService {

}
