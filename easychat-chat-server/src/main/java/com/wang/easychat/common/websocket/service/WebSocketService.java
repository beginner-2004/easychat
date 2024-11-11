package com.wang.easychat.common.websocket.service;

import io.netty.channel.Channel;

/**
 * @ClassDescription:
 * @Author:Wangzd
 * @Date: 2024/11/11
 **/
public interface WebSocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);
}
