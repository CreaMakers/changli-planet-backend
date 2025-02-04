package com.hayaizo.chatsystem.common.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hayaizo.chatsystem.common.constant.MQConstant;
import com.hayaizo.chatsystem.common.strategy.AbstractMsgHandler;
import com.hayaizo.chatsystem.common.strategy.MsgHandlerFactory;
import com.hayaizo.chatsystem.dto.MsgSendMessageDTO;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.WSBaseResp;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.mapper.ChatGroupUserMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import com.hayaizo.chatsystem.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    @Autowired
    private ChatGroupUserMapper chatGroupUserMapper;
    @Autowired
    private ChatGroupMessageMapper chatGroupMessageMapper;
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(MsgSendMessageDTO msgSendMessageDTO) {
        Integer msgID = msgSendMessageDTO.getMsgId();
        // 通过消息ID，查询到具体的消息
        ChatGroupMessage chatGroupMessage = chatGroupMessageMapper.selectById(msgID);
        // 通过房间信息查询房间有哪些人，获取所有的ID，再推送给所有在线的用户
        Integer groupId = chatGroupMessage.getGroupId();
        LambdaQueryWrapper<ChatGroupUser> queryWrapper = Wrappers.lambdaQuery(ChatGroupUser.class)
                .eq(ChatGroupUser::getGroupId, groupId);
        List<ChatGroupUser> chatGroupUsers = chatGroupUserMapper.selectList(queryWrapper);
        // 只要userID
        List<Integer> memberUidList = chatGroupUsers.stream()
                .map(ChatGroupUser::getUserId)
                .collect(Collectors.toList());
        // TODO 获取房间信息 后期改成缓存
        // 构建前端响应对象
        ChatMessageResp chatMessageResp = new ChatMessageResp();
        chatMessageResp.setUid(chatGroupMessage.getSenderId());
        ChatMessageResp.Message message = new ChatMessageResp.Message();
        message.setId(chatGroupMessage.getMessageId());
        message.setRoomId(chatGroupMessage.getGroupId());
        message.setSendTime(chatGroupMessage.getCreateTime());
        message.setType(chatGroupMessage.getMessageType());
        AbstractMsgHandler msgHandler = MsgHandlerFactory.get(chatGroupMessage.getMessageType());
        Object object = msgHandler.showMsg(chatGroupMessage);
        if(Objects.nonNull(object)){
            message.setBody(object);
        }
        chatMessageResp.setMessage(message);

        // 发送给所有在线用户
        WSBaseResp<ChatMessageResp> objectWSBaseResp = new WSBaseResp<>();
        objectWSBaseResp.setType(chatGroupMessage.getMessageType());
        objectWSBaseResp.setData(chatMessageResp);

        // 丢给线程池去消费
        webSocketService.sendToUids(objectWSBaseResp,memberUidList);
    }

}
