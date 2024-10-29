package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.mapper.UserStatsMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserStats;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    public GeneralResponse getStats(String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                .eq(UserStats::getUserId, userId);
        UserStats userStats = baseMapper.selectOne(queryWrapper);
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.DATA_RETRIEVED)
                .data(userStats)
                .build();
    }

    @Override
    public GeneralResponse getStatsById(String userId) {
        LambdaQueryWrapper<UserStats> queryWrapper = Wrappers.lambdaQuery(UserStats.class)
                .eq(UserStats::getUserId, userId);
        UserStats userStats = baseMapper.selectOne(queryWrapper);
        if(Objects.isNull(userStats)){
            return GeneralResponse.builder()
                    .code(HttpCode.NO_CONTENT)
                    .msg(ErrorMessage.DATABASE_ERROR)
                    .data(null)
                    .build();
        }
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.DATA_RETRIEVED)
                .data(userStats)
                .build();
    }

    @Override
    public GeneralResponse setStudentNumber(String studentNumber,String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        lambdaUpdate()
                .eq(UserStats::getUserId, userId)
                .set(UserStats::getStudentNumber, studentNumber).update();
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.USER_UPDATED)
                .data(true)
                .build();
    }
}
