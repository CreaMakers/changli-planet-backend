package com.hayaizo.chatsystem.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hayaizo.chatsystem.common.strategy.AbstractMsgHandler;
import com.hayaizo.chatsystem.common.strategy.MsgHandlerFactory;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.mapper.ChatGroupUserMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import com.hayaizo.chatsystem.service.ChatService;
import com.hayaizo.chatsystem.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void sendMsg(ChatMessageReq request, Integer uid) {
        // 首先判断是否拥有房间的权限
        if(!check(uid,request.getRoomId())){
            // 出错了
        }
        // 用户在群里
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.get(request.getMsgType());
        Integer msgID = msgHandler.checkAndSaveMsg(request, uid);
        // TODO 开始推送消息，后面要改成异步的（RocketMQ）
        onMessage(msgID);
    }

    private void onMessage(Integer msgID) {
        ChatGroupMessage chatGroupMessage = chatGroupMessageMapper.selectById(msgID);
        // TODO 获取房间信息 后期改成缓存
        // 构建前端响应对象
        ChatMessageResp.Message message = new ChatMessageResp.Message();
        message.setId(msgID);
        message.setGroupID(chatGroupMessage.getGroupId());
        message.setSendTime(new Date());
        message.setType(message.getType());
        message.setBody(chatGroupMessage.getMessageContent());

        ChatMessageResp chatMessageResp = ChatMessageResp.builder()
                .fromUser(chatGroupMessage.getSenderId())
                .message(message)
                .build();

        // 发送给所有在线用户
        WSBaseResp<ChatMessageResp> objectWSBaseResp = new WSBaseResp<>();
        objectWSBaseResp.setType(message.getType());
        objectWSBaseResp.setData(chatMessageResp);

        webSocketService.sendToAllOnline(objectWSBaseResp);
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
