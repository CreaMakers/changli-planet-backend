package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.ViolationRecordMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.ViolationRecord;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.ViolationRecordService;
import com.creamakers.usersystem.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GeneralResponse> getViolations(String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(username);
        Integer userId = user.getUserId();
        return getViolationsByUserId(userId);
    }

    @Override
    public ResponseEntity<GeneralResponse> getViolationsByUserId(Integer userId) {
        LambdaQueryWrapper<ViolationRecord> queryWrapper = Wrappers.lambdaQuery(ViolationRecord.class)
                .eq(ViolationRecord::getUserId, userId);
        ViolationRecord violationRecord = baseMapper.selectOne(queryWrapper);

        if(Objects.isNull(violationRecord)){
            return createResponseEntity(HttpStatus.OK,HttpCode.OK,SuccessMessage.NO_VIOLATION_INFO,null);

        }

        return createResponseEntity(HttpStatus.OK,HttpCode.OK,SuccessMessage.DATA_RETRIEVED,violationRecord);
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
