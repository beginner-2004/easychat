package com.wang.easychat.common.chat.service.strategy.mark;

import com.wang.easychat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @ClassDescription: 点踩标记策略类
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
@Component
public class DisLikeStrategy extends AbstractMsgMarkStrategy{
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    protected void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        // 取消点赞动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }
}
