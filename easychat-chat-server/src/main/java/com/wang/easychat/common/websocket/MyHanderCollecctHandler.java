package com.wang.easychat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.util.Optional;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/14
 **/
public class MyHanderCollecctHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOptional = Optional.ofNullable(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            // 如果token存在
            tokenOptional.ifPresent(token -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token));
            request.setUri(urlBuilder.getPath().toString());
        }
        ctx.fireChannelRead(msg);
    }
}
