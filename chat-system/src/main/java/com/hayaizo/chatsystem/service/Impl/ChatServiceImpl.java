package com.hayaizo.chatsystem.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hayaizo.chatsystem.common.event.MessageSendEvent;
import com.hayaizo.chatsystem.common.strategy.AbstractMsgHandler;
import com.hayaizo.chatsystem.common.strategy.MsgHandlerFactory;
import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.dto.request.ChatMessageReq;
import com.hayaizo.chatsystem.dto.response.ChatMessageResp;
import com.hayaizo.chatsystem.dto.response.CursorPageBaseResp;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.mapper.ChatGroupUserMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import com.hayaizo.chatsystem.service.ChatGroupMessageService;
import com.hayaizo.chatsystem.service.ChatService;
import com.hayaizo.chatsystem.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatGroupUserMapper chatGroupUserMapper;
    @Autowired
    private ChatGroupMessageMapper chatGroupMessageMapper;
    @Autowired
    private ChatGroupMessageService chatGroupMessageService;
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
        // TODO 开始推送消息，要改成异步的（RocketMQ） 已完成
        applicationEventPublisher.publishEvent(new MessageSendEvent(this,msgID));
        // onMessage(msgID);
        return msgID;
    }



    @Override
    public ChatMessageResp getMsgResp(Integer msgID) {
        ChatGroupMessage chatGroupMessage = chatGroupMessageMapper.selectById(msgID);
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        messageVO.setId(msgID);
        messageVO.setRoomId(chatGroupMessage.getGroupId());
        messageVO.setSendTime(chatGroupMessage.getCreateTime());
        messageVO.setType(chatGroupMessage.getMessageType());
        // 通过工厂+策略模式来动态获取到返回的数据内容
        AbstractMsgHandler msgHandler = MsgHandlerFactory.get(chatGroupMessage.getMessageType());
        if(Objects.nonNull(msgHandler)){
            messageVO.setBody(msgHandler.showMsg(chatGroupMessage));
        }
        ChatMessageResp chatMessageResp = ChatMessageResp.builder()
                .uid(chatGroupMessage.getSenderId())
                .message(messageVO)
                .build();
        return chatMessageResp;
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request) {
        // 校验请求参数，确保 roomId 不为 null
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID 不能为空");
        }

        // 如果游标为 null，意味着是第一次请求，从第一页开始
        Long cursor;
        if(request.getCursor().equals("null")){
            cursor = 0L;
        }else{
            cursor = Long.valueOf(request.getCursor());
        }

        // 创建查询条件，指定要查询的聊天组和用户是否删除
        LambdaQueryWrapper<ChatGroupMessage> queryWrapper = Wrappers.lambdaQuery(ChatGroupMessage.class)
                .eq(ChatGroupMessage::getGroupId, request.getRoomId()) // 根据房间ID查询
                .orderByDesc(ChatGroupMessage::getCreateTime); // 按发送时间降序排序

        // 通过 MyBatis-Plus 分页查询，自动根据 pageSize 和 cursor 进行分页
        Page<ChatGroupMessage> page = new Page<>(cursor, request.getPageSize());
        IPage<ChatGroupMessage> messagePage = chatGroupMessageMapper.selectPage(page, queryWrapper);

        // 将查询结果转换为响应对象
        List<ChatMessageResp> chatMessageResps = messagePage.getRecords().stream()
                .map(message -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setUid(message.getSenderId());
                    ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
                    messageVO.setId(message.getMessageId());
                    messageVO.setRoomId(message.getGroupId());
                    messageVO.setType(message.getMessageType());
                    messageVO.setSendTime(message.getCreateTime());
                    AbstractMsgHandler msgHandler = MsgHandlerFactory.get(message.getMessageType());
                    Object object = msgHandler.showMsg(message);
                    if(Objects.nonNull(object)){
                        messageVO.setBody(object);
                    }
                    resp.setMessage(messageVO);
                    return resp;
                })
                .collect(Collectors.toList());

        // 创建响应对象并填充数据
        CursorPageBaseResp<ChatMessageResp> response = new CursorPageBaseResp<>();
        response.setList(chatMessageResps);
        response.setCursor(generateNextCursor(messagePage.getRecords())); // 设置下一页的游标

        return response;
    }


    // 生成下一个游标的方法
    private String generateNextCursor(List<ChatGroupMessage> messages) {
        // 如果没有消息，返回 null（说明分页结束）
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        // 使用当前页最后一条消息的 messageId 作为下一个游标
        // 也可以选择使用 createTime（时间戳），这取决于你使用哪一个字段作为分页的标准
        return String.valueOf(messages.get(messages.size() - 1).getMessageId());
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
