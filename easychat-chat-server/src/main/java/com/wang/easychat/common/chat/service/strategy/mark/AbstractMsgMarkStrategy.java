package com.wang.easychat.common.chat.service.strategy.mark;

import com.wang.easychat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.wang.easychat.common.chat.domain.entity.MessageMark;
import com.wang.easychat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.wang.easychat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.wang.easychat.common.chat.service.IMessageMarkService;
import com.wang.easychat.common.common.domain.enums.YesOrNoEnum;
import com.wang.easychat.common.common.event.MessageMarkEvent;
import com.wang.easychat.common.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassDescription: 消息标记抽象类
 * @Author:Wangzd
 * @Date: 2024/12/9
 **/
public abstract class AbstractMsgMarkStrategy {
    
    @Autowired
    private IMessageMarkService messageMarkService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    protected abstract MessageMarkTypeEnum getTypeEnum();

    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }


    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    protected void doMark(Long uid, Long msgId){
        execute(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void doUnMark(Long uid, Long msgId){
        execute(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    protected void execute(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum){
        Integer markType = getTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkService.get(uid, msgId, markType);
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK){
            // 取消标记 而且没有记录，直接快速返回
            return;
        }
        // 插入或修改数据
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        boolean modify = messageMarkService.saveOrUpdate(insertOrUpdate);
        if (modify) {
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
        }
    }

    private Integer transformAct(Integer actType){
        if (actType == 1){
            return YesOrNoEnum.YES.getStatus();
        }else if (actType == 2){
            return YesOrNoEnum.NO.getStatus();
        }
        throw new BusinessException("动作类型: 1确认 2取消");
    }
}
