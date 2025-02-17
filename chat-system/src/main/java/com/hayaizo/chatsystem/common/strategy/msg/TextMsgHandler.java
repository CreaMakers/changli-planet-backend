package com.hayaizo.chatsystem.common.strategy.msg;

import com.hayaizo.chatsystem.common.Enum.MessageTypeEnum;
import com.hayaizo.chatsystem.common.strategy.AbstractMsgHandler;
import com.hayaizo.chatsystem.dto.request.TextMsgReq;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {

    @Autowired
    private ChatGroupMessageMapper chatGroupMessageMapper;

    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * 保存额外信息
     * @param message
     * @param body
     */
    @Override
    public void saveMsg(ChatGroupMessage message, TextMsgReq body) {
        // 根据消息ID更新数据库表
        Integer messageId = message.getMessageId();
        ChatGroupMessage updateMessage = new ChatGroupMessage();
        updateMessage.setMessageId(messageId);

        // TODO 需要用AC自动机处理敏感词
        updateMessage.setMessageContent(body.getContent());

        // TODO 处理回复消息的逻辑，需要设置消息间隔数，设置回复消息ID

        // TODO @功能
        chatGroupMessageMapper.updateById(updateMessage);
    }

    @Override
    public Object showMsg(ChatGroupMessage msg) {
        return null;
    }

    @Override
    public Object showReplyMsg(ChatGroupMessage msg) {
        return null;
    }

    @Override
    public String showContactMsg(ChatGroupMessage msg) {
        return "";
    }
}
