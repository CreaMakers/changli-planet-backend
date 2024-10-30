package com.creamakers.usersystem.service.impl;


import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.request.PasswordUpdateRequest;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.dto.response.PasswordUpdateResp;
import com.creamakers.usersystem.exception.UserServiceException;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.UserAuthService;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.CookieUtil;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.PasswordEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserAuthServiceImpl implements UserAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserStatsService userStatsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private JwtUtil jwtUtil;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        logger.info("Attempting to register user: {}", username);

        try {
            User existingUser = userService.getUserByUsername(username);
            if (existingUser != null) {
                logger.warn("User registration failed: Username already exists for {}", username);
                return createResponseEntity(HttpStatus.CONFLICT, HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null);
            }

            String encodedPassword = passwordEncoderUtil.encodePassword(registerRequest.getPassword());

            User newUser = userService.createUser(registerRequest, encodedPassword);
            int rowsAffected = userService.saveUser(newUser);

            if (rowsAffected > 0) {
                userProfileService.initializeUserProfile(newUser.getUserId());
                userStatsService.initializeUserStats(newUser.getUserId());

                logger.info("User registered successfully: {}", newUser.getUsername());
                return createResponseEntity(HttpStatus.CREATED, HttpCode.CREATED, SuccessMessage.USER_REGISTERED, null);
            } else {
                logger.error("User registration failed: Database insert operation returned 0 rows");
                return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.OPERATION_FAILED, null);
            }
        } catch (DuplicateKeyException dke) {
            logger.error("Duplicate username found during registration: {}", username, dke);
            return createResponseEntity(HttpStatus.CONFLICT, HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null);
        } catch (DataAccessException dae) {
            logger.error("Database error during user registration for user: {}", username, dae);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        } catch (Exception e) {
            logger.error("Unexpected error during user registration for user: {}", username, e);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.OPERATION_FAILED, null);
        }
    }


    @Override
    public ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId) {
        logger.info("Attempting login for user: {}", loginRequest.getUsername());
        if (deviceId == null || deviceId.isEmpty()) {
            logger.warn("Login attempt failed: Missing deviceId for user {}", loginRequest.getUsername());
            return createResponseEntity(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, "Missing device ID", null);
        }

        try {
            // 获取用户信息
            User foundUser = userService.getUserByUsername(loginRequest.getUsername());

            if (foundUser == null) {
                logger.warn("Login attempt failed: User not found for username {}", loginRequest.getUsername());
                return createResponseEntity(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null);
            }

            // 验证密码
            if (passwordEncoderUtil.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                long timestamp = System.currentTimeMillis();

                // 生成访问和刷新令牌
                String accessToken = jwtUtil.generateAccessToken(foundUser.getUsername(), deviceId, timestamp);
                String refreshToken = jwtUtil.generateRefreshToken(foundUser.getUsername(), deviceId, timestamp);

                // 将刷新令牌存储到缓存
                userService.cacheRefreshToken(foundUser.getUsername(), deviceId, refreshToken);

                // 将 accessToken 放入 Authorization 头中
                return createResponseWithAuthHeader(accessToken, HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_LOGGED_IN, null);
            } else {
                logger.warn("Login attempt failed: Invalid credentials for user {}", loginRequest.getUsername());
                return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_CREDENTIALS, null);
            }
        } catch (DataAccessException dataAccessException) {
            logger.error("Database error during login for user {}: {}", loginRequest.getUsername(), dataAccessException.getMessage(), dataAccessException);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        } catch (Exception generalException) {
            logger.error("Unexpected error during login for user {}: {}", loginRequest.getUsername(), generalException.getMessage(), generalException);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.OPERATION_FAILED, null);
        }
    }



    @Override
    public ResponseEntity<GeneralResponse> updatePassword(PasswordUpdateRequest request, String accessToken) {
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, ErrorMessage.PASSWORD_NOT_SAME, false);
        }

        String username = jwtUtil.getUserNameFromToken(accessToken);
        String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

        String encryptedPassword = passwordEncoderUtil.encodePassword(request.getNewPassword());

        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);

        // 执行更新操作
        boolean isUpdated = userService.updateUser(user);

        // 检查是否更新成功
        if (!isUpdated) {
            return createResponseEntity(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, false);
        }


        userService.cacheRefreshToken(user.getUsername(), deviceIDFromToken, accessToken);

        PasswordUpdateResp passwordUpdateResp = new PasswordUpdateResp();
        passwordUpdateResp.setAccessToken(jwtUtil.generateAccessToken(username, deviceIDFromToken, System.currentTimeMillis()));

        return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_UPDATED, passwordUpdateResp);
    }


    @Override
    public ResponseEntity<GeneralResponse> checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest) {
        logger.info("Checking username availability: {}", usernameCheckRequest.getUsername());

        try {
            User user = userService.getUserByUsername(usernameCheckRequest.getUsername());
            if (user != null) {
                logger.warn("Username {} is already taken", usernameCheckRequest.getUsername());

                return createResponseEntity(HttpStatus.CONFLICT, HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null);
            }

            logger.info("Username {} is available", usernameCheckRequest.getUsername());

            return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.OPERATION_SUCCESSFUL, null);
        } catch (DataAccessException dae) {
            logger.error("Database error during username availability check: {}", usernameCheckRequest.getUsername(), dae);

            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        }

    }


    @Override
    public ResponseEntity<GeneralResponse> quit(String accessToken, String deviceId) {
        logger.info("Quit user: {}", accessToken);

        String username = jwtUtil.getUserNameFromToken(accessToken);
        if (username == null) {
            return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null);
        }

        userService.deleteRefreshToken(username, deviceId);
        userService.addAccessToBlacklist(accessToken);
        return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_LOGGED_OUT, null);
    }


    @Override
    public ResponseEntity<GeneralResponse> refreshAuth(String auth) {
        try {
            // 获取 accessToken
            String accessToken = auth;

            // 验证 accessToken 的有效性
            if (!jwtUtil.validateToken(accessToken)) {
                return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_TOKEN, null);
            }

            // 从 accessToken 中提取用户名和设备ID
            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

            // 获取用户
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return createResponseEntity(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null);
            }

            // 将 accessToken 缓存
            userService.cacheRefreshToken(user.getUsername(), deviceIDFromToken, accessToken);



            // 返回成功响应
            return createResponseWithAuthHeader(accessToken,HttpStatus.OK, HttpCode.OK, SuccessMessage.TOKEN_REFRESHED, null);
        } catch (Exception e) {
            logger.error("Error refreshing authentication token: {}", e.getMessage(), e);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.OPERATION_FAILED, null);
        }
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

    @Override
    public ResponseEntity<GeneralResponse> createResponseWithAuthHeader(String accessToken, HttpStatus status, String code, String msg, Object data) {
        return ResponseEntity
                .status(status)
                .header("Authorization", "Bearer " + accessToken)
                .body(GeneralResponse.builder()
                        .code(code)
                        .msg(msg)
                        .data(data)
                        .build());
    }

}
