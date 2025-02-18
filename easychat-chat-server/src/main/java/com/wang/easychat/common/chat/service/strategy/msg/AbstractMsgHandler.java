package com.wang.easychat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.wang.easychat.common.chat.domain.entity.Message;
import com.wang.easychat.common.chat.domain.enums.MessageTypeEnum;
import com.wang.easychat.common.chat.domain.vo.req.ChatMessageReq;
import com.wang.easychat.common.chat.service.IMessageService;
import com.wang.easychat.common.chat.service.adapter.MessageAdapter;
import com.wang.easychat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * @ClassDescription: 消息处理器抽象类
 * @Author:Wangzd
 * @Date: 2024/11/29
 **/
public abstract class AbstractMsgHandler<Req> {
    @Autowired
    private IMessageService messageService;

    private Class<Req> bodyClass;

    @PostConstruct
    private void init(){
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型
     */
    abstract MessageTypeEnum getMsgTypeEnum();

    /**
     * 子类拓展校验消息合法性
     */
    protected void checkMsg(Req body, Long roomId, Long uid){

    }

    protected void chatAi(Req body, Long uid, Long msgId, Long roomId){

    }

    /**
     * 校验并保存消息
     */
    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid){
        Req body = this.toBean(request.getBody());
        // 统一检验
        AssertUtil.allCheckValidateThrow(body);
        // 自类扩展检验
        checkMsg(body, request.getRoomId(), uid);
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        // 统一保存
        messageService.save(insert);
        chatAi(body, uid, insert.getId(), request.getRoomId());
        // 子类扩展保存
        saveMsg(insert, body);
        return insert.getId();
    }

    /**
     * 兼容不同的类型
     */
    private Req toBean(Object body){
        if (bodyClass.isAssignableFrom(body.getClass())){
            return (Req) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    /**
     * 子类拓展保存消息逻辑
     */
    protected abstract void saveMsg(Message insert, Req body);

    /**
     * 展示消息
     */
    public abstract Object showMsg(Message message);

    /**
     * 展示被回复的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表消息
     */
    public abstract String showContactMsg(Message msg);
}
