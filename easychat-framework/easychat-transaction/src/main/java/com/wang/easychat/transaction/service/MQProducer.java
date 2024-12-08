package com.wang.easychat.transaction.service;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import com.wang.easychat.transaction.annotation.SecureInvoke;

/**
 * @ClassDescription: 发送mq工具类
 * @Author:Wangzd
 * @Date: 2024/12/3
 **/
public class MQProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendMsg(String topic, Object body){
        Message<Object> build = MessageBuilder.withPayload(body).build();
        rocketMQTemplate.send(topic, build);
    }

    /**
     * 发送消息
     */
    @SecureInvoke
    public void sendSecureMsg(String topic, Object body, Object key){
        Message<Object> build = MessageBuilder
                .withPayload(body)
                .setHeader("KEYS", key)
                .build();
        rocketMQTemplate.send(topic, build);
    }
}
