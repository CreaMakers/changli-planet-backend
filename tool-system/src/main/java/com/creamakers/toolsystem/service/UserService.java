package com.creamakers.toolsystem.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.toolsystem.po.User;
import com.creamakers.toolsystem.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
