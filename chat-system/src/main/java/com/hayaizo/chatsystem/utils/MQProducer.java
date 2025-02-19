package com.hayaizo.chatsystem.utils;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MQProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 通过消息队列发送消息
     */
    public void sendTransactionMsg(String topic, Object body, Object key) {
        Message<Object> build = MessageBuilder
                .withPayload(body)
                .setHeader("KEYS", key)
                .build();
        rocketMQTemplate.send(topic, build);
    }

}
