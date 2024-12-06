package com.wang.easychat.common.user.service.impl;

import com.wang.easychat.common.common.constant.MQConstant;
import com.wang.easychat.common.common.domain.dto.PushMessageDTO;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassDescription: 推送消息类
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@Service
public class PushService {

    @Autowired
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList){
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }
}
