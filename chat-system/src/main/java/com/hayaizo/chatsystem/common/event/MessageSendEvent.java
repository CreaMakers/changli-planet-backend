package com.hayaizo.chatsystem.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageSendEvent extends ApplicationEvent {
    private Integer msgId;
    public MessageSendEvent(Object source, Integer msgId) {
        super(source);
        this.msgId = msgId;
    }
}
