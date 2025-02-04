package com.hayaizo.chatsystem.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.mapper.ChatGroupMessageMapper;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import com.hayaizo.chatsystem.service.ChatGroupMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatGroupMessageServiceImpl extends ServiceImpl<ChatGroupMessageMapper, ChatGroupMessage> implements ChatGroupMessageService {
}