package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.mapper.ViolationRecordMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.ViolationRecord;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.ViolationRecordService;
import com.creamakers.usersystem.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ViolationRecordServiceImpl extends ServiceImpl<ViolationRecordMapper, ViolationRecord> implements ViolationRecordService {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public ViolationRecordServiceImpl(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public GeneralResponse getViolations(String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        return getViolationsByUserId(userId);
    }

    public GeneralResponse getViolationsByUserId(Integer userId) {
        LambdaQueryWrapper<ViolationRecord> queryWrapper = Wrappers.lambdaQuery(ViolationRecord.class)
                .eq(ViolationRecord::getUserId, userId);
        ViolationRecord violationRecord = baseMapper.selectOne(queryWrapper);

        if(Objects.isNull(violationRecord)){
            return GeneralResponse.builder()
                    .code(HttpCode.OK)
                    .msg(SuccessMessage.NO_VIOLATION_INFO)
                    .data(null)
                    .build();
        }

        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.DATA_RETRIEVED)
                .data(violationRecord)
                .build();
    }
}
