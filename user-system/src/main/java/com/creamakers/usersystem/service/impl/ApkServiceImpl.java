package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.ApkResp;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.ApkUpdateMapper;
import com.creamakers.usersystem.po.ApkUpdate;
import com.creamakers.usersystem.service.ApkService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.creamakers.usersystem.consts.SuccessMessage.ALREADY_LATEST_VERSION_MESSAGE;
import static com.creamakers.usersystem.consts.SuccessMessage.FETCH_LATEST_APK_VERSION_SUCCESS_MESSAGE;

@Service
public class ApkServiceImpl implements ApkService {
    @Autowired
    private ApkUpdateMapper apkUpdateMapper;
    @Override
    public ResponseEntity<GeneralResponse> checkApkVersion(Integer versionCode, String versionName) {
        LambdaQueryWrapper<ApkUpdate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApkUpdate::getVersionCode).last("LIMIT 1");
        ApkUpdate latestApkUpdate = apkUpdateMapper.selectOne(wrapper);
        if (latestApkUpdate == null||latestApkUpdate != null && latestApkUpdate.getVersionCode().equals(versionCode) ) {
            return buildResponse(HttpStatus.OK, HttpCode.OK, ALREADY_LATEST_VERSION_MESSAGE, null);
        }
        ApkResp apkResp = new ApkResp();
        BeanUtils.copyProperties(latestApkUpdate,apkResp);
        return buildResponse(HttpStatus.OK, HttpCode.OK,FETCH_LATEST_APK_VERSION_SUCCESS_MESSAGE,apkResp);
    }

    private ResponseEntity<GeneralResponse> buildResponse(HttpStatus status, String code, String msg, Object data) {
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .code(code)
                        .msg(msg)
                        .data(data)
                        .build());
    }
}
