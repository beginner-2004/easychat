package com.wang.easychat.common.user.service.adapter;

import com.wang.easychat.common.chat.domain.entity.dto.ChatMsgRecallDTO;
import com.wang.easychat.common.chat.domain.vo.resp.ChatMessageResp;
import com.wang.easychat.common.chat.service.ChatService;
import com.wang.easychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.wang.easychat.common.websocket.domain.vo.resp.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/12/4
 **/
@Component
public class WSAdapter {
    @Autowired
    private ChatService chatService;

    /**
     * 构造消息返回体
     */
    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    /**
     * 构造撤回消息返回体
     */
    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO recallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(recallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }
}
