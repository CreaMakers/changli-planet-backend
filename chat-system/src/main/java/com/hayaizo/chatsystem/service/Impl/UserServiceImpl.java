package com.hayaizo.chatsystem.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hayaizo.chatsystem.mapper.UserMapper;
import com.hayaizo.chatsystem.po.User;
import com.hayaizo.chatsystem.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
