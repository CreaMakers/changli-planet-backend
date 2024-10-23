package com.creamakers.usersystem.service;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.usersystem.dto.GeneralResponse;
import com.creamakers.usersystem.dto.LoginRequest;
import com.creamakers.usersystem.po.User;

public interface UserService {
    GeneralResponse login(LoginRequest loginRequest);
    User getUserByUsername(String username); // 根据用户名获取用户
}