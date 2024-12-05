package com.hayaizo.chatsystem.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hayaizo.chatsystem.mapper.ChatGroupUserMapper;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import com.hayaizo.chatsystem.service.ChatGroupUserService;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupUserServiceImpl extends ServiceImpl<ChatGroupUserMapper, ChatGroupUser> implements ChatGroupUserService {
}
