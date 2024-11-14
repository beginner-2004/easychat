package com.wang.easychat.common.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.entity.User;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.user.service.WXMsgService;
import com.wang.easychat.common.user.service.adapter.TextBuilder;
import com.wang.easychat.common.user.service.adapter.UserAdapter;
import com.wang.easychat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;

/**
 * @ClassDescription: 用户适配器
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {

    @Value("${wx.mp.callback}")
    private String callback;
    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


    @Autowired
    private IUserService userService;
    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Autowired
    @Lazy
    private WebSocketService webSocketService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)){
            return null;
        }
        User user = userService.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        // 注册成功并且授权
        if (registered && authorized){
            // todo 走登录成功逻辑 通过code找到channel推送消息
            webSocketService.scanLoginSuccess(code, user.getId());
            return TextBuilder.build("欢迎回来！" + user.getName(), wxMpXmlMessage);
        }
        // 用户未注册，就先注册
        if (!registered){
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
            RedisUtils.set(RedisKey.getKey(RedisKey.WAIT_LOGIN_USER_CODE, insert.getId()), code);
        }


        // 没登录成功
        // 推送链接让用户授权
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录：<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    /**
     * 用户授权
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userService.getByOpenId(userInfo.getOpenid());
        // 更新用户信息
        if (StringUtils.isEmpty(user.getName())){
            fillUserInfo(user.getId(), userInfo);
        }

        //找到对应的code
        Integer code = RedisUtils.get(RedisKey.getKey(RedisKey.WAIT_LOGIN_USER_CODE, user.getId()), Integer.class);

        // 确认登录
        webSocketService.scanLoginSuccess(code, user.getId());

    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User update = UserAdapter.buildAuthorizeUser(uid, userInfo);
        // todo 用户昵称违规或重复  随机昵称逻辑
        try {
            userService.updateById(update);
            return;
        } catch (DuplicateKeyException e) {
            log.info("fill userInfo duplicate uid:{},info:{}", uid, userInfo);
        } catch (Exception e) {
            log.error("fill userInfo fail uid:{},info:{}", uid, userInfo);
        }
        update.setName("用户" + update.getId());
        userService.updateById(update);
    }
}
