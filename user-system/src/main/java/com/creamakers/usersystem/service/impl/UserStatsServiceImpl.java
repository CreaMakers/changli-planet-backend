package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.UserStatsMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserStats;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.JwtUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserStatsServiceImpl extends ServiceImpl<UserStatsMapper, UserStats> implements UserStatsService {

    private static final Logger logger = LoggerFactory.getLogger(UserStatsServiceImpl.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserStatsMapper userStatsMapper;

    @Autowired
    public UserStatsServiceImpl(JwtUtil jwtUtil, UserService userService, UserStatsMapper userStatsMapper) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userStatsMapper = userStatsMapper;
    }

    @Override
    public ResponseEntity<GeneralResponse> getStats(String accessToken) {
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            logger.info("Fetching stats for user: {}", username);

            User user = userService.getUserByUsername(username);
            Integer userId = user.getUserId();

            LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                    .eq(UserStats::getUserId, userId);
            UserStats userStats = baseMapper.selectOne(queryWrapper);

            logger.info("Stats retrieved successfully for user: {}", username);
            return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.DATA_RETRIEVED, userStats);
        } catch (Exception e) {
            logger.error("Error fetching stats: {}", e.getMessage());
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> getStatsById(String userId) {
        try {
            logger.info("Fetching stats by user ID: {}", userId);

            LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                    .eq(UserStats::getUserId, userId);
            UserStats userStats = baseMapper.selectOne(queryWrapper);

            if (Objects.isNull(userStats)) {
                logger.warn("No stats found for user ID: {}", userId);
                return createResponseEntity(HttpStatus.NO_CONTENT, HttpCode.NO_CONTENT, ErrorMessage.DATABASE_ERROR, null);
            }

            logger.info("Stats retrieved successfully for user ID: {}", userId);
            return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.DATA_RETRIEVED, userStats);
        } catch (Exception e) {
            logger.error("Error fetching stats by ID: {}", e.getMessage());
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> setStudentNumber(String studentNumber, String accessToken) {
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            logger.info("Updating student number for user: {}", username);

            User user = userService.getUserByUsername(username);
            Integer userId = user.getUserId();

            lambdaUpdate()
                    .eq(UserStats::getUserId, userId)
                    .set(UserStats::getStudentNumber, studentNumber)
                    .update();

            logger.info("Student number updated successfully for user: {}", username);
            return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_UPDATED, user);
        } catch (Exception e) {
            logger.error("Error updating student number: {}", e.getMessage());
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        }
    }

    @Override
    public Boolean initializeUserStats(User user) {
        try {
            logger.info("Initializing stats for user ID: {}", user.getUserId());

            UserStats userStats = UserStats.builder()
                    .userId(user.getUserId())
                    .account(user.getUsername())
                    .studentNumber("")
                    .articleCount(0)
                    .commentCount(0)
                    .statementCount(0)
                    .likedCount(0)
                    .coinCount(0)
                    .xp(0)
                    .quizType(0)
                    .build();

            boolean result = save(userStats);
            logger.info("Stats initialized successfully for user ID: {}", user.getUserId());
            return result;
        } catch (Exception e) {
            logger.error("Error initializing stats for user ID: {}", user.getUserId(), e);
            return false;
        }
    }

    @Override
    public Boolean updateUserStats(UserStats userStats) {
        try {
            boolean success = userStatsMapper.update(null, createUpdateWrapper(userStats)) > 0;
            if (success) {
                logger.info("User password updated successfully for username: {}", userStats.getAccount());
            } else {
                logger.warn("No user found to update for username: {}", userStats.getAccount());
            }
            return success;
        } catch (DataAccessException e) {
            logger.error("Database error while updating user: {}", userStats.getAccount(), e);
            throw new MyBatisSystemException(e);
        }
    }

    private Wrapper<UserStats> createUpdateWrapper(UserStats userStats) {
        return Wrappers.lambdaUpdate(UserStats.class)
                .eq(UserStats::getUserId, userStats.getUserId())
                .set(UserStats::getAccount, userStats.getAccount());
    }

    @Override
    public ResponseEntity<GeneralResponse> createResponseEntity(HttpStatus status, String code, String msg, Object data) {
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .code(code)
                        .msg(msg)
                        .data(data)
                        .build());
    }
}
