package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameUpdateRequest;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.RedisUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
                .mailbox(registerRequest.getEmail())
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
    public boolean updateUserPassword(User user) {
        try {
            boolean success = userMapper.update(null, UpdateUserPasswordWrapper(user)) > 0;
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

    private LambdaUpdateWrapper<User> UpdateUserPasswordWrapper(User user) {
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
    public boolean updateUserUsername(UsernameUpdateRequest request) {
        try {
            boolean success = userMapper.update(null, UpdateUserUsernameWrapper(request)) > 0;
            if (success) {
                logger.info("Username updated successfully for username: {}", request.getOldUsername());
            } else {
                logger.warn("No user found to update for username: {}", request.getOldUsername());
            }
            return success;
        } catch (DataAccessException e) {
            logger.error("Database error while updating user: {}", request.getOldUsername(), e);
            throw new MyBatisSystemException(e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        List<User> users;

        try {
            users = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .eq(User::getMailbox, email));
        } catch (DataAccessException e) {
            logger.error("Database error while fetching user by email: {}", email, e);
            throw new MyBatisSystemException(e);
        }

        if (users.isEmpty()) {
            return null;
        } else if (users.size() > 1) {
            String message = "Multiple users (" + users.size() + ") found with email: " + email;
            logger.error("Data integrity error: {}", message);

            // 创建并抛出一个新的异常实例
            DataIntegrityViolationException exception =
                    new DataIntegrityViolationException(message);

            // 打印堆栈以便调试
            logger.error("Throwing exception", exception);

            throw exception; // 直接抛出异常
        } else {
            return users.get(0);
        }
    }

    private Wrapper<User> UpdateUserUsernameWrapper(UsernameUpdateRequest request) {
        return Wrappers.<User>lambdaUpdate(User.class)
                .eq(User::getUsername, request.getOldUsername())
                .set(User::getUsername, request.getNewUsername());
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
