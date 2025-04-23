package com.creamakers.usersystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.response.ApkResp;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.ApkUpdateMapper;
import com.creamakers.usersystem.po.ApkUpdate;
import com.creamakers.usersystem.service.ApkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.creamakers.usersystem.consts.RedisKeyConst.LATEST_APK_URL_KEY;
import static com.creamakers.usersystem.consts.SuccessMessage.*;

@Service
public class ApkServiceImpl implements ApkService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthServiceImpl.class);
    @Autowired
    private ApkUpdateMapper apkUpdateMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseEntity<GeneralResponse> checkApkVersion(Integer versionCode, String versionName) {
//        String apkUrl = redisTemplate.opsForValue().get(LATEST_APK_URL_KEY);
        String apkUrl = null;
        logger.info("Checking for latest APK URL in Redis...");
//        if (apkUrl == null) {
        logger.info("No APK URL found in Redis. Querying database for the latest APK version...");
        LambdaQueryWrapper<ApkUpdate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApkUpdate::getVersionCode).last("LIMIT 1");
        ApkUpdate latestApkUpdate = apkUpdateMapper.selectOne(wrapper);
        if (latestApkUpdate == null || (latestApkUpdate != null && latestApkUpdate.getVersionCode() <= versionCode)) {
            logger.info("No new APK version available. The current version is the latest.");
            return buildResponse(HttpStatus.OK, HttpCode.OK, ALREADY_LATEST_VERSION_MESSAGE, null);
        }
        if (latestApkUpdate.getVersionCode() == null || latestApkUpdate.getVersionName() == null) {
            logger.info("Fetched latest APK data from the database");
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, FETCH_LATEST_APK_VERSION_FAILURE_MESSAGE, null);
        }
        apkUrl = latestApkUpdate.getDownloadUrl();
        logger.info("Fetched latest APK URL from the database: {}", apkUrl);
        redisTemplate.opsForValue().set(LATEST_APK_URL_KEY, apkUrl, 7, TimeUnit.DAYS);
        logger.info("Stored the latest APK URL in Redis with a 7-day expiration.");
//        } else {
//            logger.info("Found APK URL in Redis: {}", apkUrl);
//        }
        ApkResp apkResp = new ApkResp();
        apkResp.setVersionCode(latestApkUpdate.getVersionCode());
        apkResp.setVersionName(latestApkUpdate.getVersionName());
        apkResp.setUpdateMessage(latestApkUpdate.getUpdateMessage().replace("\\\\n", "\\n"));
        apkResp.setCreateTime(latestApkUpdate.getCreateTime());
        apkResp.setDownloadUrl(apkUrl);
        return buildResponse(HttpStatus.OK, HttpCode.OK, FETCH_LATEST_APK_VERSION_SUCCESS_MESSAGE, apkResp);
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
