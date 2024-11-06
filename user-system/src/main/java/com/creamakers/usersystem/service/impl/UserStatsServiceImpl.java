package com.creamakers.usersystem.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserStatsServiceImpl extends ServiceImpl<UserStatsMapper, UserStats> implements UserStatsService {

    private final JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    public UserStatsServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<GeneralResponse>  getStats(String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                .eq(UserStats::getUserId, userId);
        UserStats userStats = baseMapper.selectOne(queryWrapper);
        return createResponseEntity(HttpStatus.OK,HttpCode.OK,SuccessMessage.DATA_RETRIEVED,userStats);
    }

    @Override
    public ResponseEntity<GeneralResponse> getStatsById(String userId) {
        LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                .eq(UserStats::getUserId, userId);
        UserStats userStats = baseMapper.selectOne(queryWrapper);
        if(Objects.isNull(userStats)){
            return createResponseEntity(HttpStatus.NO_CONTENT,HttpCode.NO_CONTENT,ErrorMessage.DATABASE_ERROR,null);
        }
        return createResponseEntity(HttpStatus.OK,HttpCode.OK,SuccessMessage.DATA_RETRIEVED,userStats);

    }

    @Override
    public ResponseEntity<GeneralResponse> setStudentNumber(String studentNumber,String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        lambdaUpdate()
                .eq(UserStats::getUserId, userId)
                .set(UserStats::getStudentNumber, studentNumber).update();
        return createResponseEntity(HttpStatus.OK,HttpCode.OK,SuccessMessage.USER_UPDATED,user);
    }

    @Override
    public Boolean initializeUserStats(Integer userId) {
        UserStats userStats = UserStats.builder()
                .userId(userId)
                .studentNumber("")
                .articleCount(0)
                .commentCount(0)
                .statementCount(0)
                .likedCount(0)
                .coinCount(0)
                .xp(0)
                .quizType(0)
                .build();
       return save(userStats);
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
