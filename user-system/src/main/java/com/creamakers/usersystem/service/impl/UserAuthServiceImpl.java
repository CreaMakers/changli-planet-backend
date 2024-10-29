package com.creamakers.usersystem.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.request.PasswordUpdateRequest;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.dto.response.PasswordUpdateResp;
import com.creamakers.usersystem.dto.response.RefreshAuthResp;
import com.creamakers.usersystem.exception.UserServiceException;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.po.UserStats;
import com.creamakers.usersystem.service.UserAuthService;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.creamakers.usersystem.consts.SuccessMessage.USER_LOGGED_OUT;
import static com.creamakers.usersystem.consts.SuccessMessage.USER_TOKEN_REFRESH;


@Service
public class UserAuthServiceImpl implements UserAuthService {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${REFRESH_TOKEN_PREFIX}")
    private String REFRESH_TOKEN_PREFIX;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserStatsService userStatsService;
    @Autowired
    private UserService userService;




    @Override
    public ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId,HttpServletResponse response) {
        logger.info("Attempting login for user: {}", loginRequest.getUsername());
        // 处理空的 deviceId
        if (deviceId == null || deviceId.isEmpty()) {
            logger.warn("Login attempt failed: Missing deviceId for user {}", loginRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GeneralResponse(HttpCode.BAD_REQUEST, "Missing device ID", null));
        }

        try {
            // 获取用户信息
            User foundUser = userService.getUserByUsername(loginRequest.getUsername());

            if (foundUser == null) {
                logger.warn("Login attempt failed: User not found for username {}", loginRequest.getUsername());
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new GeneralResponse(HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null));
            }

            // 验证密码
            if (passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                long timestamp = System.currentTimeMillis();

                // 生成访问和刷新令牌
                String accessToken = jwtUtil.generateToken(foundUser.getUsername(), deviceId, timestamp);
                String refreshToken = jwtUtil.generateRefreshToken(foundUser.getUsername(), deviceId, timestamp);

                // 将刷新令牌存储到缓存
                userService.cacheRefreshToken(foundUser.getUsername(), deviceId, refreshToken);

                // 设置刷新令牌 Cookie
                Cookie accessCookie = new Cookie("accessToken", accessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(accessCookie); // 将 Cookie 添加到响应

                // 构建响应
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(GeneralResponse.builder()
                                .code(HttpCode.OK)
                                .msg(SuccessMessage.USER_LOGGED_IN)
                                .data(null)
                                .build());
            } else {
                logger.warn("Login attempt failed: Invalid credentials for user {}", loginRequest.getUsername());
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new GeneralResponse(HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_CREDENTIALS, null));
            }
        } catch (DataAccessException dataAccessException) {
            logger.error("Database error during login for user {}: {}", loginRequest.getUsername(), dataAccessException.getMessage(), dataAccessException);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GeneralResponse(HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.DATABASE_ERROR, null));
        } catch (Exception generalException) {
            logger.error("Unexpected error during login for user {}: {}", loginRequest.getUsername(), generalException.getMessage(), generalException);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GeneralResponse(HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.OPERATION_FAILED, null));
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user: {}", registerRequest.getUsername());
        try {
            if (userService.getUserByUsername(registerRequest.getUsername()) != null) {
                logger.warn("User registration failed: Username already exists for {}", registerRequest.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new GeneralResponse(HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null));
            }

            // 加密用户密码
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            User newUser = User.builder()
                    .username(registerRequest.getUsername())
                    .password(encodedPassword)
                    .isAdmin((byte) 0)
                    .isDeleted((byte) 0)
                    .isBanned((byte) 0)
                    .build();

            int rowsAffected = userService.addUser(newUser);
            if (rowsAffected > 0) {
                // 获取刚插入的用户
                User user = userService.getUserByUsername(registerRequest.getUsername());
                // 初始化 UserProfile
                UserProfile userProfile = UserProfile.builder()
                        .userId(user.getUserId())
                        .avatarUrl("https://pic.imgdb.cn/item/671e5e17d29ded1a8c5e0dbe.jpg")
                        .bio("这个人很懒，没有写任何描述")
                        .userLevel(1)
                        .gender(2)
                        .grade("大学一年级")
                        .location("中国")
                        .birthDate(new Date())
                        .build();
                userProfileService.save(userProfile);

                // 初始化 UserStats
                UserStats userStats = UserStats.builder()
                        .userId(user.getUserId())
                        .studentNumber("")
                        .articleCount(0)
                        .commentCount(0)
                        .statementCount(0)
                        .likedCount(0)
                        .coinCount(0)
                        .xp(0)
                        .quizType(0)
                        .build();
                userStatsService.save(userStats);

                logger.info("User registered successfully: {}", newUser.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(GeneralResponse.builder()
                                .code(HttpCode.CREATED)
                                .msg(SuccessMessage.USER_REGISTERED)
                                .data(newUser.getUserId())
                                .build());
            } else {
                logger.error("User registration failed: Database insert operation returned 0 rows");
                throw new UserServiceException(ErrorMessage.OPERATION_FAILED, HttpCode.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException dae) {
            logger.error("Database error during user registration: {}", registerRequest.getUsername(), dae);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GeneralResponse.builder()
                            .code(HttpCode.INTERNAL_SERVER_ERROR)
                            .msg(ErrorMessage.DATABASE_ERROR)
                            .data(null)
                            .build());
        } catch (Exception e) {
            logger.error("Unexpected error during user registration: {}", registerRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GeneralResponse.builder()
                            .code(HttpCode.INTERNAL_SERVER_ERROR)
                            .msg(ErrorMessage.OPERATION_FAILED)
                            .data(null)
                            .build());
        }
    }


    @Override
    public GeneralResponse refreshAuth(String accessToken) {
        // 日志记录：开始刷新 token 的操作
        logger.info("Attempting to refresh token for accessToken: {}", accessToken);

        // 验证 accessToken 是否有效
        boolean isValid = jwtUtil.validateToken(accessToken);

        try {
            // 从 accessToken 中提取 username 和设备ID
            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);
            Long currentTimeMillis = System.currentTimeMillis();

            // 创建新的refresh、access的token
            String newAccessToken = jwtUtil.generateToken(username, deviceIDFromToken, currentTimeMillis);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, deviceIDFromToken, currentTimeMillis);

            // token写入redis缓存
            userService.cacheRefreshToken(username, deviceIDFromToken, newRefreshToken);

            return GeneralResponse.builder()
                    .code(HttpCode.OK)
                    .msg(USER_TOKEN_REFRESH)
                    .data(newAccessToken)
                    .build();


        } catch (Exception e) {
            logger.error("Failed to extract information from accessToken", e);
            return GeneralResponse.builder()
                    .code(HttpCode.BAD_REQUEST)
                    .msg("Invalid access token")
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse updatePassword(PasswordUpdateRequest request, String accessToken) {
        // 确认新密码和确认密码是否一致
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            return GeneralResponse.builder()
                    .code(HttpCode.BAD_REQUEST)
                    .msg(ErrorMessage.PASSWORD_NOT_SAME)
                    .data(false)
                    .build();
        }

        // 从 token 中获取用户名
        String username = jwtUtil.getUserNameFromToken(accessToken);
        String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

        // 加密新密码（可选）
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword()); // 假设你有 passwordEncoder

        // 设置更新条件和新密码
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getUsername, username)
                .set(User::getPassword, encryptedPassword);

        // 执行更新操作
        int rowsUpdated = userMapper.update(null, updateWrapper);

        // 检查是否更新成功
        if (rowsUpdated == 0) {
            return GeneralResponse.builder()
                    .code(HttpCode.NOT_FOUND)
                    .msg(ErrorMessage.USER_NOT_FOUND)
                    .data(false)
                    .build();
        }
        // 删除旧缓存，更新新缓存，返回新token
        String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceIDFromToken;
        stringRedisTemplate.delete(key);
        Long currentTimeMillis = System.currentTimeMillis();
        String newAccessToken = jwtUtil.generateToken(username, deviceIDFromToken, currentTimeMillis);
        String newRefreshToken = jwtUtil.generateRefreshToken(username, deviceIDFromToken, currentTimeMillis);

        userService.cacheRefreshToken(username, deviceIDFromToken, newRefreshToken);

        PasswordUpdateResp passwordUpdateResp = new PasswordUpdateResp();
        passwordUpdateResp.setAccessToken(newAccessToken);

        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.USER_UPDATED)
                .data(passwordUpdateResp)
                .build();
    }



    private GeneralResponse generateAndStoreNewTokens(String username, String deviceIDFromToken, Long currentTimeMillis, String refreshTokenKey) {
        // 生成新的 accessToken
        String newAccessToken = jwtUtil.generateToken(username, deviceIDFromToken, currentTimeMillis);

        // 生成新的 refreshToken
        String newRefreshToken = jwtUtil.generateRefreshToken(username, deviceIDFromToken, currentTimeMillis);

        // 日志记录：删除 Redis 中旧的 refreshToken
        logger.info("Deleting old refresh token for key: {}", refreshTokenKey);

        // 从 Redis 中删除旧的 refreshToken
        stringRedisTemplate.delete(refreshTokenKey);

        // 将新的 refreshToken 存储到 Redis 中，键为 refreshTokenKey，值为 newRefreshToken
        logger.info("Storing new refresh token in Redis for key: {}", refreshTokenKey);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, newRefreshToken);

        // 返回响应：包含新的 accessToken，刷新操作成功
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(USER_TOKEN_REFRESH)
                .data(new RefreshAuthResp(newAccessToken))
                .build();
    }


    @Override
    public GeneralResponse checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest) {
        logger.info("Checking username availability: {}", usernameCheckRequest.getUsername());
        try {
            User user = userService.getUserByUsername(usernameCheckRequest.getUsername());
            if (user != null) {
                logger.warn("Username {} is already taken", usernameCheckRequest.getUsername());
                return new GeneralResponse(HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null);
            }
            logger.info("Username {} is available", usernameCheckRequest.getUsername());
            return new GeneralResponse(HttpCode.OK, SuccessMessage.OPERATION_SUCCESSFUL, null);
        } catch (DataAccessException dae) {
            logger.error("Database error during username availability check: {}", usernameCheckRequest.getUsername(), dae);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.DATABASE_ERROR)
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse quit(String accessToken) {
        // 应该就不用判断token是否有效了
        // 获取username、deviceID
        String username = jwtUtil.getUserNameFromToken(accessToken);
        String deviceId = jwtUtil.getDeviceIDFromToken(accessToken);
        String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceId;
        stringRedisTemplate.delete(key);
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(USER_LOGGED_OUT)
                .data(null)
                .build();
    }


}
