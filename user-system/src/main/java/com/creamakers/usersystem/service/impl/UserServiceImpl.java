package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.RedisUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;



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


    @Override
    public void cacheRefreshToken(String username, String deviceId, String refreshToken) {
        logger.info("Caching refresh token for user {}", username);
        redisUtil.storeRefreshToken(username, deviceId, refreshToken);
    }


    @Override
    public boolean isRefreshTokenExpired(String username, String deviceId){
        logger.info("Checking if refresh token is expired for user {}", username);
        return redisUtil.isRefreshTokenExpired(username, deviceId);
    }


    @Override
    public void deleteRefreshToken(String username, String deviceId) {
        logger.info("Deleting refresh token for user {}", username);
        redisUtil.deleteRefreshToken(username, deviceId);
    }

    @Override
    public void addAccessToBlacklist(String accessToken) {
        logger.info("Adding access to blacklist for user {}", accessToken);
        redisUtil.addAccessToBlacklist(accessToken);
    }

    @Override
    public String getCachedAccessTokenFromBlack(String accessToken) {
        return redisUtil.getCachedAccessTokenFromBlack(accessToken);
    }


    @Override
    public User createUser(RegisterRequest registerRequest, String encodedPassword) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword)
                .isAdmin((byte) 0) // 默认不是管理员
                .isDeleted((byte) 0) // 默认未删除
                .isBanned((byte) 0) // 默认未禁止
                .build();
    }


    @Override
    public boolean updateUser(User user) {
        // 创建更新条件
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getUsername, user.getUsername())
                .set(User::getPassword, user.getPassword());

        // 执行更新
        return userMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public int saveUser(User User) {
        return userMapper.insert(User);
    }


}
