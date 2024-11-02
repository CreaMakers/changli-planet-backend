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
    public ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId, String accessToken) {
        // 检查 deviceId 是否存在
        if (deviceId == null || deviceId.isEmpty()) {
            logger.warn("Login attempt failed: Missing deviceId for user {}", loginRequest != null ? loginRequest.getUsername() : "Unknown");
            return createResponseEntity(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, "Missing device ID", null);
        }

        // 检查 accessToken 的状态（是否为空或在黑名单中）
        String cachedAccessToken = accessToken != null ? userService.getCachedAccessTokenFromBlack(accessToken) : null;

        // 如果 accessToken 存在且不在黑名单，则执行无感刷新
        if (accessToken != null && cachedAccessToken == null) {
            // 获取时间戳
            Long timeStampFromToken = jwtUtil.getTimeStampFromToken(accessToken);
            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceIdFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

            // 确保 deviceId 和 username 一致
            if (deviceIdFromToken.equals(deviceId)) {
                // 获取与 accessToken 相关联的 refreshToken
                String refreshToken = userService.getFreshTokenByUsernameAndDevicedId(username,deviceId);

                // 检查 refreshToken 是否存在并且时间戳一致
                if (refreshToken != null && !userService.isRefreshTokenExpired(username, deviceId) &&
                        timeStampFromToken != null && jwtUtil.getTimeStampFromToken(refreshToken).equals(timeStampFromToken)) {
                    logger.info("Token is valid, performing seamless refresh for device: {}", deviceId);
                    return handleLoginWithNewTokens(username, deviceId); // 生成新的 accessToken 和 refreshToken
                }
            }
        }

        // 如果 accessToken 不存在或在黑名单中，则检查是否提供了登录请求进行密码登录
        if (loginRequest != null) {
            logger.info("Access token is missing or invalid; attempting password login for user: {}", loginRequest.getUsername());
            User foundUser = userService.getUserByUsername(loginRequest.getUsername());
            if (foundUser == null) {
                logger.warn("Login attempt failed: User not found for username {}", loginRequest.getUsername());
                return createResponseEntity(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null);
            }

            // 检查密码是否匹配
            if (passwordEncoderUtil.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                return handleLoginWithNewTokens(foundUser.getUsername(), deviceId);
            } else {
                logger.warn("Login attempt failed: Invalid credentials for user {}", loginRequest.getUsername());
                return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_CREDENTIALS, null);
            }
        }

        // 如果 accessToken 和 loginRequest 都未提供，返回错误
        logger.warn("Login attempt failed: Both access token and login request are missing.");
        return createResponseEntity(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, "Both access token and login request are missing", null);
    }



    private ResponseEntity<GeneralResponse> handleLoginWithNewTokens(String username, String deviceId) {
        long timestamp = System.currentTimeMillis();
        String newAccessToken = jwtUtil.generateAccessToken(username, deviceId, timestamp);
        String newRefreshToken = jwtUtil.generateRefreshToken(username, deviceId, timestamp);

        userService.cacheRefreshToken(username, deviceId, newRefreshToken);
        return createResponseWithAuthHeader(newAccessToken, HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_LOGGED_IN, null);
    }



    @Override
    public ResponseEntity<GeneralResponse> updatePassword(PasswordUpdateRequest request, String accessToken) {
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, ErrorMessage.PASSWORD_NOT_SAME, false);
        }

        String username = jwtUtil.getUserNameFromToken(accessToken);

        String encryptedPassword = passwordEncoderUtil.encodePassword(request.getNewPassword());

        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);

        boolean isUpdated = userService.updateUser(user);

        if (!isUpdated) {
            return createResponseEntity(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, false);
        }

        return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_UPDATED, null);
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

            return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_NOT_EXITS, null);
        } catch (DataAccessException dae) {
            logger.error("Database error during username availability check: {}", usernameCheckRequest.getUsername(), dae);

            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null);
        }

    }

    @Override
    public ResponseEntity<GeneralResponse> quit(String accessToken, String deviceId) {
        String username = jwtUtil.getUserNameFromToken(accessToken);

        if (username == null) {
            return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null);
        }
        logger.info("Quit user: {}", username);


        userService.deleteRefreshToken(username, deviceId);
        userService.addAccessToBlacklist(accessToken);
        return createResponseEntity(HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_LOGGED_OUT, null);
    }

    @Override
    public ResponseEntity<GeneralResponse> refreshAuth(String auth) {
        try {
            String accessToken = auth;
            if (!jwtUtil.validateToken(accessToken)) {
                return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_TOKEN, null);
            }

            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

            if (userService.isRefreshTokenExpired(username, deviceIDFromToken)) {
                return createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED, null);
            }

            long timestamp = System.currentTimeMillis();
            String newAccessToken = jwtUtil.generateAccessToken(username, deviceIDFromToken, timestamp);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, deviceIDFromToken, timestamp);
            userService.cacheRefreshToken(username, deviceIDFromToken, newRefreshToken);

            return createResponseWithAuthHeader(newAccessToken, HttpStatus.OK, HttpCode.OK, SuccessMessage.TOKEN_REFRESHED, null);
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
