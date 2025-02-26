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
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        try {
            return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        } catch (DataAccessException e) {
            logger.error("Database error while fetching user by username: {}", username, e);
            throw new MyBatisSystemException(e);
        }
    }

    @Override
    public int addUser(User newUser) {
        logger.info("Adding new user: {}", newUser.getUsername());
        return insertUser(newUser);
    }

    private int insertUser(User user) {
        try {
            return userMapper.insert(user);
        } catch (DataAccessException e) {
            logger.error("Database error while inserting user: {}", user.getUsername(), e);
            throw new MyBatisSystemException(e);
        }
    }

    @Override
    public User createUserAndInsert(RegisterRequest registerRequest, String encodedPassword) {
        // 构建用户对象
        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword)
                .isAdmin((byte) 0)
                .mailbox(registerRequest.getMailbox())
                .isDeleted((byte) 0)
                .isBanned((byte) 0)

                .build();


        int rowsInserted = addUser(newUser);
        if (rowsInserted > 0) {
            logger.info("User created successfully with userId: {}", newUser.getUserId());
        } else {
            logger.warn("Failed to insert user: {}", newUser.getUsername());
        }

        return newUser;
    }

    @Override
    public boolean updateUser(User user) {
        try {
            boolean success = userMapper.update(null, createUpdateWrapper(user)) > 0;
            if (success) {
                logger.info("User password updated successfully for username: {}", user.getUsername());
            } else {
                logger.warn("No user found to update for username: {}", user.getUsername());
            }
            return success;
        } catch (DataAccessException e) {
            logger.error("Database error while updating user: {}", user.getUsername(), e);
            throw new MyBatisSystemException(e);
        }
    }

    private LambdaUpdateWrapper<User> createUpdateWrapper(User user) {
        return Wrappers.lambdaUpdate(User.class)
                .eq(User::getUsername, user.getUsername())
                .set(User::getPassword, user.getPassword());
    }

    @Override
    public void cacheRefreshToken(String username, String deviceId, String refreshToken) {
        logger.info("Caching refresh token for user: {} on device: {}", username, deviceId);
        redisUtil.storeRefreshToken(username, deviceId, refreshToken);
    }

    @Override
    public boolean isRefreshTokenExpired(String username, String deviceId) {
        logger.info("Checking if refresh token is expired for user: {} on device: {}", username, deviceId);
        return redisUtil.isRefreshTokenExpired(username, deviceId);
    }

    @Override
    public void deleteRefreshToken(String username, String deviceId) {
        logger.info("Deleting refresh token for user: {} on device: {}", username, deviceId);
        redisUtil.deleteRefreshToken(username, deviceId);
    }

    @Override
    public void addAccessToBlacklist(String accessToken) {
        logger.info("Adding access token to blacklist: {}", accessToken);
        redisUtil.addAccessToBlacklist(accessToken);
    }

    @Override
    public String getFreshTokenByUsernameAndDevicedId(String username, String deviceId) {
        logger.info("Fetching refresh token for user: {} on device: {}", username, deviceId);
        return redisUtil.getFreshTokenByUsernameAndDeviceId(username, deviceId);
    }

    @Override
    public String getCachedAccessTokenFromBlack(String accessToken) {
        logger.info("Checking if access token is blacklisted: {}", accessToken);
        return redisUtil.getCachedAccessTokenFromBlack(accessToken);
    }

    @Override
    public int saveUser(User user) {
        logger.info("Saving user: {}", user.getUsername());
        return insertUser(user);
    }
}
