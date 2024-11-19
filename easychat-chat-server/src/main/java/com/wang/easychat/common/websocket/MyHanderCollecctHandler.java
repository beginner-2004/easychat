package com.wang.easychat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
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
            // 移除后面拼接的所有参数
            request.setUri(urlBuilder.getPath().toString());
            // 取用户ip
            String ip = request.headers().get("X-Real-IP");
            if (StringUtils.isBlank(ip)){
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }

            // ip保存到channel附件
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            // 处理器只需要用一次
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }
}
