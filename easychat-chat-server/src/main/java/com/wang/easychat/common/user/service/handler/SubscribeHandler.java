package com.wang.easychat.common.user.service.handler;


import com.wang.easychat.common.user.service.WXMsgService;
import com.wang.easychat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/8
 **/
@Component
public class SubscribeHandler extends AbstractHandler {
    @Autowired
    private WXMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = wxMsgService.scan(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        try {
            return new TextBuilder().build("感谢关注", wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }


}
