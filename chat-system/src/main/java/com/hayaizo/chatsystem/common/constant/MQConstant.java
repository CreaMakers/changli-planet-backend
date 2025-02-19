package com.hayaizo.chatsystem.common.constant;

/**
 * 消息队列常量
 */
public interface MQConstant {

    /**
     * 消息发送mq
     */
    String SEND_MSG_TOPIC = "chat_send_msg";
    String SEND_MSG_GROUP = "chat_send_msg_group";

    /**
     * push用户
     */
    String PUSH_TOPIC = "websocket_push";
    String PUSH_GROUP = "websocket_push_group";
}
