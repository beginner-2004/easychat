package com.wang.easychat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wang.easychat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.wang.easychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.wang.easychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.wang.easychat.common.websocket.domain.vo.resp.WSLoginUrl;
import com.wang.easychat.common.websocket.service.WebSocketService;
import com.wang.easychat.common.websocket.service.adapter.WebSocektAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassDescription: 专门管理websocket的逻辑，包括推送消息，拉取消息
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 管理所有用户的连接(登录用户/游客)
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP  = new ConcurrentHashMap<>();

    public static final Duration DURATION = Duration.ofHours(1);
    public static final int MAXIMUM_SIZE = 1000;
    /**
     * 临时保存 登录code 和 channel 的映射关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 生成随机码
        Integer code = generateLoginCode(channel);
        // 找微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 把二维码推送给前端
        sendMsg(channel, WebSocektAdapter.buildResp(wxMpQrCodeTicket));
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    /**
     * 将 随机码 和 channel 绑定在一起
     * @param channel
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while (Objects.isNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel))); // 如果不存在这个随机码，返回null，跳出循环

        return code;
    }
}
