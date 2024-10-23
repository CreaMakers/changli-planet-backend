package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.creamakers.usersystem.dto.GeneralResponse;
import com.creamakers.usersystem.dto.LoginRequest;
import com.creamakers.usersystem.dto.LoginSuccessData;

import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.RedisUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper; // 使用 UserMapper 进行数据库操作

    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil

    @Autowired
    private RedisUtil redisUtil;

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String REFRESH_TOKEN_PREFIX;

    @Override
    public GeneralResponse login(LoginRequest loginRequest) {
        User user = getUserByUsername(loginRequest.getUsername());

        // 验证用户名和密码
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            // 生成 JWT 和刷新 token
            String accessToken = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // 缓存刷新 token
            cacheRefreshToken(user.getUsername(), refreshToken);

            // 创建成功响应数据
            LoginSuccessData successData = LoginSuccessData.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .expires_in("3600")
                    .build();

            // 创建成功响应
            return GeneralResponse.builder()
                    .code("200")
                    .msg("Login successful")
                    .data(successData)
                    .build();
        } else {
            // 创建失败响应
            return GeneralResponse.builder()
                    .code("400")
                    .msg("Invalid username or password")
                    .data(null)
                    .build();
        }

    }

    // 缓存刷新 token 到 Redis
    private void cacheRefreshToken(String username, String refreshToken) {
        redisUtil.setValue(REFRESH_TOKEN_PREFIX + username, refreshToken, 30, TimeUnit.DAYS);
    }

    // 刷新 token 方法
    public GeneralResponse refreshToken(String refreshToken) {
        // 验证 refreshToken
        String username = jwtUtil.extractUsername(refreshToken);
        String cachedRefreshToken = (String) redisUtil.getValue(REFRESH_TOKEN_PREFIX + username);

        if (cachedRefreshToken != null && cachedRefreshToken.equals(refreshToken)) {
            // 生成新的 accessToken 和 refreshToken
            String newAccessToken = jwtUtil.generateToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            // 更新缓存中的刷新 token
            cacheRefreshToken(username, newRefreshToken);

            // 创建成功响应数据
            LoginSuccessData successData = LoginSuccessData.builder()
                    .access_token(newAccessToken)
                    .refresh_token(newRefreshToken)
                    .expires_in("3600")
                    .build();

            // 创建成功响应
            return GeneralResponse.builder()
                    .code("200")
                    .msg("Token refreshed successfully")
                    .data(successData)
                    .build();
        } else {
            // 创建失败响应
            return GeneralResponse.builder()
                    .code("401")
                    .msg("Invalid refresh token")
                    .data(null)
                    .build();
        }
    }

    @Override
    public User getUserByUsername(String username) {
        List<User> users = userMapper.findUserByUsername(username);

        if (users.isEmpty()) {
            // 如果没有找到用户，返回 null 或者抛出异常
            return null; // 或者 throw new UsernameNotFoundException("User not found: " + username);
        }

        if (users.size() > 1) {
            // 如果找到多条记录，抛出异常
            throw new MyBatisSystemException(new RuntimeException("Multiple users found with the same username: " + username));
        }

        return users.get(0); // 返回唯一的用户
    }



}
