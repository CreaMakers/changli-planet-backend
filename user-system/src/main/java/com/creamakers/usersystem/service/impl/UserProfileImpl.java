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
import com.creamakers.usersystem.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
