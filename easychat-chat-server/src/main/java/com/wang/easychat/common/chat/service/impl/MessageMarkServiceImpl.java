package com.wang.easychat.common.chat.service.impl;

import com.wang.easychat.common.chat.domain.entity.MessageMark;
import com.wang.easychat.common.chat.mapper.MessageMarkMapper;
import com.wang.easychat.common.chat.service.IMessageMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.easychat.common.common.domain.enums.NormalOrNoEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author wang
 * @since 2024-11-29
 */
@Service
public class MessageMarkServiceImpl extends ServiceImpl<MessageMarkMapper, MessageMark> implements IMessageMarkService {

    /**
     * 根据 id集合 获取 MessageMark
     * @param msgIds
     * @return
     */
    @Override
    public List<MessageMark> getValidMarkByMsgIdBatch(List<Long> msgIds) {
        return lambdaQuery()
                .in(MessageMark::getUid, msgIds)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .list();
    }
}
