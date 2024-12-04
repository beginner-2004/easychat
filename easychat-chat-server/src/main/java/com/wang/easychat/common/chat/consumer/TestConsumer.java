package com.wang.easychat.common.chat.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @ClassDescription: mq测试类
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
@RocketMQMessageListener(consumerGroup = "test-group", topic = "test1-topic")
@Component
public class TestConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("收到消息{}" + s);
    }
}
