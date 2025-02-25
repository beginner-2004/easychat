package com.wang.easychat.common.user.service.adapter;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
public class TextBuilder {

    public static WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage) {
        WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
        return m;
    }
}
