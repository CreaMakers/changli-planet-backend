package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.UserProfileMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.HUAWEIOBSUtil;
import com.creamakers.usersystem.util.JwtUtil;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
public class UserProfileImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileImpl.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;


    @Autowired
    public UserProfileImpl(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<GeneralResponse> getProfile(String accessToken) {
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            logger.info("Fetching profile for user: {}", username);

            User user = userService.getUserByUsername(username);
            UserProfile userProfile = getUserProfile(user.getUserId());

            logger.info("Profile retrieved successfully for user: {}", username);
            return buildResponse(HttpStatus.OK, HttpCode.OK, SuccessMessage.DATA_RETRIEVED, userProfile);
        } catch (Exception e) {
            logger.error("Error fetching profile: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "Failed to retrieve profile", null);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> getProfileByID(String userID) {
        try {
            logger.info("Fetching profile by user ID: {}", userID);
            UserProfile userProfile = getUserProfile(Integer.valueOf(userID));
            logger.info("Profile retrieved successfully for user ID: {}", userID);
            return buildResponse(HttpStatus.OK, HttpCode.OK, SuccessMessage.DATA_RETRIEVED, userProfile);
        } catch (Exception e) {
            logger.error("Error fetching profile by ID: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "Failed to retrieve profile by ID", null);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> updateInfo(UserProfileRequest request, String accessToken) {
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            logger.info("Updating profile for user: {}", username);

            User user = userService.getUserByUsername(username);
            UserProfile userProfile = new UserProfile();
            BeanUtils.copyProperties(request, userProfile);

            lambdaUpdate()
                    .eq(UserProfile::getUserId, user.getUserId())
                    .eq(UserProfile::getIsDeleted, 0)
                    .update(userProfile);

            logger.info("Profile updated successfully for user: {}", username);
            return buildResponse(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_UPDATED, userProfile);
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "Failed to update profile", null);
        }
    }

    @Override
    public Boolean initializeUserProfile(Integer userId) {
        try {
            logger.info("Initializing profile for user ID: {}", userId);
            UserProfile userProfile = UserProfile.builder()
                    .userId(userId)
                    .avatarUrl("https://pic.imgdb.cn/item/671e5e17d29ded1a8c5e0dbe.jpg")
                    .bio("这个人很懒，没有写任何描述")
                    .userLevel(1)
                    .gender(2)
                    .grade("大学一年级")
                    .location("中国")
                    .birthDate(new Date())
                    .build();

            boolean result = save(userProfile);
            logger.info("Profile initialized successfully for user ID: {}", userId);
            return result;
        } catch (Exception e) {
            logger.error("Error initializing profile for user ID: {}", userId, e);
            return false;
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> saveAvatar(MultipartFile avatar, String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);
        logger.info("Updating avatar for user: {}", username);

        try {
            // 校验图片格式
            String originalFileName = avatar.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".png"; // 默认使用 .png
            if (!HUAWEIOBSUtil.isValidImageExtension(fileExtension)) {
                return buildResponse(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, "Invalid image format", null);
            }

            // 上传头像到华为云 OBS
            String fileUrl = HUAWEIOBSUtil.uploadAvatar(avatar, username);

            // 更新用户头像URL
            User user = userService.getUserByUsername(username);
            UserProfile userProfile = new UserProfile();
            userProfile.setAvatarUrl(fileUrl);
            lambdaUpdate()
                    .eq(UserProfile::getUserId, user.getUserId())
                    .eq(UserProfile::getIsDeleted, 0)
                    .update(userProfile);

            logger.info("Profile updated successfully for user: {}", username);

            // 通过临时授权方式实现图片处理
            long expireSeconds = 3600L; // 1小时
            String temporaryUrl = HUAWEIOBSUtil.generateTemporaryUrl("userAvatar/" + username + ".png", expireSeconds);

            return buildResponse(HttpStatus.OK, HttpCode.OK, "Avatar uploaded successfully", temporaryUrl);

        } catch (ObsException e) {
            // 华为云OBS客户端异常
            logger.error("OBS upload failed: HTTP Code: {}, Error Code: {}, Error Message: {}",
                    e.getResponseCode(), e.getErrorCode(), e.getErrorMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "OBS upload failed", null);
        } catch (IOException e) {
            // 文件操作异常
            logger.error("File operation failed: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "File operation failed", null);
        } catch (Exception e) {
            // 其他异常
            logger.error("Unexpected error: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "Unexpected error", null);
        }
    }

    // 校验图片扩展名是否合法
    private boolean isValidImageExtension(String extension) {
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String validExtension : validExtensions) {
            if (extension.equalsIgnoreCase(validExtension)) {
                return true;
            }
        }
        return false;
    }

    private UserProfile getUserProfile(Integer userId) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>()
                        .eq(UserProfile::getUserId, userId)
                        .eq(UserProfile::getIsDeleted, 0)
        );
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
