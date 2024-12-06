package com.wang.easychat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.wang.easychat.common.common.constant.RedisKey;
import com.wang.easychat.common.common.utils.RedisUtils;
import com.wang.easychat.common.user.domain.enums.ChatActiveStatusEnum;
import com.wang.easychat.common.user.service.IUserService;
import com.wang.easychat.common.websocket.domain.enums.WSReqTypeEnum;
import com.wang.easychat.common.websocket.domain.vo.req.WSBaseReq;
import com.wang.easychat.common.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Sharable
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private IUserService userService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        userService = SpringUtil.getBean(IUserService.class);
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
         userOffline(ctx.channel());
    }

    /**
     * 用户下线统一处理
     */
    private void userOffline(Channel channel){
        Long uid = webSocketService.getOnLineUserMap(channel);
        try{
            userService.setUserActiveStatus(uid, ChatActiveStatusEnum.OFFLINE.getStatus());
        }catch (Exception e){
            log.info("Error => {}", e);
        }
        webSocketService.remove(channel);
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid));
        channel.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if(StrUtil.isNotBlank(token)){
                webSocketService.authorize(ctx.channel(), token);
            }
        }else if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE){
                // userOffline(ctx.channel());
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())){
            case AUTHORIZE:
                webSocketService.authorize(channelHandlerContext.channel(), wsBaseReq.getData());
                break;
            case HEARTBEAT:
                break;
            case LOGIN:
                webSocketService.handleLoginReq(channelHandlerContext.channel());
        }
    }
}
