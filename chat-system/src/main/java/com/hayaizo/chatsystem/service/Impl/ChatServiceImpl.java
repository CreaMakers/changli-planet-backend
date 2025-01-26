package com.hayaizo.chatsystem.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hayaizo.chatsystem.common.event.MessageSendEvent;
import com.hayaizo.chatsystem.common.strategy.AbstractMsgHandler;
import com.hayaizo.chatsystem.common.strategy.MsgHandlerFactory;
import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.CursorPageBaseResp;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.mapper.ChatGroupUserMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import com.hayaizo.chatsystem.service.ChatService;
import com.hayaizo.chatsystem.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatGroupUserMapper chatGroupUserMapper;
    @Autowired
    private ChatGroupMessageMapper chatGroupMessageMapper;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private RocketMQTemplate rocketmqTemplate;

    @Override
    public Integer sendMsg(ChatMessageReq request, Integer uid) {
        // 首先判断是否拥有房间的权限
        if(!check(uid,request.getRoomId())){
            // 出错了
            return -1;
        }
        // 用户在群里
        // 通过消息类型获取对应消息处理器
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.get(request.getMsgType());
        Integer msgID = msgHandler.checkAndSaveMsg(request, uid);
        // TODO 开始推送消息，要改成异步的（RocketMQ）
        applicationEventPublisher.publishEvent(new MessageSendEvent(this,msgID));
        // onMessage(msgID);

        return msgID;
    }

    @Override
    public ChatMessageResp getMsgResp(Integer msgID) {
        ChatGroupMessage chatGroupMessage = chatGroupMessageMapper.selectById(msgID);
        ChatMessageResp resp = new ChatMessageResp();
        resp.setFromUser(chatGroupMessage.getSenderId());
        ChatMessageResp.Message message = new ChatMessageResp.Message();
        message.setId(chatGroupMessage.getMessageId());
        message.setGroupID(chatGroupMessage.getGroupId());
        message.setType(chatGroupMessage.getMessageType());
        message.setBody(chatGroupMessage.getMessageContent());
        message.setSendTime(new Date());
        resp.setMessage(message);
        return resp;
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request) {

        return null;
    }


    private Boolean check(Integer uid, Integer roomId) {
        LambdaQueryWrapper<ChatGroupUser> chatGroupUserLambdaQueryWrapper = Wrappers.lambdaQuery(ChatGroupUser.class)
                .eq(ChatGroupUser::getGroupId, roomId)
                .eq(ChatGroupUser::getUserId, uid)
                .eq(ChatGroupUser::getIsDeleted, 0);
        ChatGroupUser chatGroupUser = chatGroupUserMapper.selectOne(chatGroupUserLambdaQueryWrapper);
        if(ObjectUtil.isNull(chatGroupUser)){
            return false;
        }
        return true;
    }
}
