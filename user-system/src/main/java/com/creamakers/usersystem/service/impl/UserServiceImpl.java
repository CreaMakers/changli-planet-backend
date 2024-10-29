package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.PasswordUpdateRequest;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;

import com.creamakers.usersystem.dto.response.PasswordUpdateResp;
import com.creamakers.usersystem.dto.response.RefreshAuthResp;
import com.creamakers.usersystem.exception.UserServiceException;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.po.UserStats;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;


import static com.creamakers.usersystem.consts.SuccessMessage.USER_LOGGED_OUT;
import static com.creamakers.usersystem.consts.SuccessMessage.USER_TOKEN_REFRESH;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String REFRESH_TOKEN_PREFIX;



    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        try {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username); // 假设 User 类中有 getUsername 方法

            return userMapper.selectOne(queryWrapper); // 返回符合条件的单个用户
        } catch (DataAccessException dae) {
            logger.error("Database error fetching user: {}", username, dae);
            throw new MyBatisSystemException(dae); // 转换为 MyBatis 特定异常
        }
    }

    public int addUser(User newUser) {
        logger.info("Inserting new user: {}", newUser);
        try {
            return userMapper.insert(newUser); // 使用 MyBatis Plus 的 insert 方法
        } catch (DataAccessException dae) {
            logger.error("Database error inserting user: {}", newUser.getUsername(), dae);
            throw new MyBatisSystemException(dae); // 转换为 MyBatis 特定异常
        }
    }


    public void cacheRefreshToken(String username, String deviceId, String refreshToken) {
        logger.info("Caching refresh token for user {}", username);
        String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceId;
        stringRedisTemplate.opsForValue().set(key, refreshToken);
        stringRedisTemplate.expire(key, 30L, TimeUnit.DAYS);
    }



}
