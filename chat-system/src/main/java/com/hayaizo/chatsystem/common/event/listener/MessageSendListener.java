package com.hayaizo.chatsystem.common.event.listener;

import com.hayaizo.chatsystem.common.constant.MQConstant;
import com.hayaizo.chatsystem.common.event.MessageSendEvent;
import com.hayaizo.chatsystem.dto.MsgSendMessageDTO;
import com.hayaizo.chatsystem.utils.MQProducer;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageSendListener {

    @Autowired
    private MQProducer mqProducer;

    /**
     * 处理消息的发送
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT,classes = MessageSendEvent.class,fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Integer msgId = event.getMsgId();
        mqProducer.sendTransactionMsg(MQConstant.SEND_MSG_TOPIC,new MsgSendMessageDTO(msgId),msgId);
    }
}
