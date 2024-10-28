package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.dto.response.LoginSuccessData;

import com.creamakers.usersystem.dto.response.RefreshAuthResp;
import com.creamakers.usersystem.exception.UserServiceException;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.po.UserStats;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.RedisUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static com.creamakers.usersystem.consts.SuccessMessage.USER_LOGGED_OUT;
import static com.creamakers.usersystem.consts.SuccessMessage.USER_TOKEN_REFRESH;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

    @Override
    public GeneralResponse login(LoginRequest loginRequest) {
        // 拿出设备ID
        String deviceId = loginRequest.getDeviceId();

        logger.info("Attempting to log in user: {}", loginRequest.getUsername());
        try {
            User user = getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                logger.warn("Login attempt failed: User not found");
                return new GeneralResponse(HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null);
            }

            // 使用 BCrypt 验证加密密码
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                Long currentTimeMillis = System.currentTimeMillis();
                // 加入时间戳 timeStamp:value
                String accessToken = jwtUtil.generateToken(user.getUsername(),deviceId,currentTimeMillis);
                String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(),deviceId,currentTimeMillis);

                cacheRefreshToken(user.getUsername(),deviceId, refreshToken);

                LoginSuccessData successData = LoginSuccessData.builder()
                        .access_token(accessToken)
                        .build();

                logger.info("User {} logged in successfully", user.getUsername());
                return GeneralResponse.builder()
                        .code(HttpCode.OK)
                        .msg(SuccessMessage.USER_LOGGED_IN)
                        .data(successData)
                        .build();
            } else {
                logger.warn("Login attempt failed: Invalid credentials for user {}", loginRequest.getUsername());
                return new GeneralResponse(HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_CREDENTIALS, null);
            }
        } catch (DataAccessException dae) {
            logger.error("Database error during login attempt for user {}: {}", loginRequest.getUsername(), dae.getMessage(), dae);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.DATABASE_ERROR)
                    .data(null)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error during login attempt for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.OPERATION_FAILED)
                    .data(null)
                    .build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user: {}", registerRequest.getUsername());
        try {
            if (userMapper.findUserByUsername(registerRequest.getUsername()) != null) {
                logger.warn("User registration failed: Username already exists for {}", registerRequest.getUsername());
                return new GeneralResponse(HttpCode.CONFLICT, ErrorMessage.USER_ALREADY_EXISTS, null);
            }

            // 加密用户密码
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            User newUser = User.builder()
                    .username(registerRequest.getUsername())
                    .password(encodedPassword)
                    .isAdmin((byte) 0)
                    .isDeleted((byte) 0)
                    .isBanned((byte)0)
                    .build();

            int rowsAffected = userMapper.insertUser(newUser);
            if (rowsAffected > 0) {
                // 获取刚插入的用户表
                User user = userMapper.findUserByUsername(registerRequest.getUsername());
                // 初始化UserProfile表
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

                // 初始化用户状态信息
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
                return GeneralResponse.builder()
                        .code(HttpCode.CREATED)
                        .msg(SuccessMessage.USER_REGISTERED)
                        .data(newUser.getUserId()) // 返回用户ID
                        .build();
            } else {
                logger.error("User registration failed: Database insert operation returned 0 rows");
                throw new UserServiceException(ErrorMessage.OPERATION_FAILED, HttpCode.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException dae) {
            logger.error("Database error during user registration: {}", registerRequest.getUsername(), dae);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.DATABASE_ERROR)
                    .data(null)
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error during user registration: {}", registerRequest.getUsername(), e);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.OPERATION_FAILED)
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse refreshAuth(String accessToken) {
        // 日志记录：开始刷新 token 的操作
        logger.info("Attempting to refresh token for accessToken: {}", accessToken);

        // 验证 accessToken 是否有效
        boolean isValid = jwtUtil.validateToken(accessToken);

       try{
           // 从 accessToken 中提取 username 和设备ID
           String username = jwtUtil.getUserNameFromToken(accessToken);
           String deviceIDFromToken = jwtUtil.getDeviceIDFromToken(accessToken);

           // 从 accessToken 中提取时间戳
           Long timeStampFromToken = jwtUtil.getTimeStampFromToken(accessToken);

           // 生成 Redis 中存储的 refreshToken 对应的键值，格式为：REFRESH_TOKEN_PREFIX + username + "-" + deviceIDFromToken
           String refreshTokenKey = REFRESH_TOKEN_PREFIX + username + "-" + deviceIDFromToken;

           // 获取当前系统时间，作为生成新 token 的时间戳
           Long currentTimeMillis = System.currentTimeMillis();

           // 如果 accessToken 仍然有效
           if (isValid) {
               // 日志记录：accessToken 仍然有效
               logger.info("Access token is valid, generating new tokens.");

               // 调用方法生成新的 accessToken 和 refreshToken 并存储到 Redis
               return generateAndStoreNewTokens(username, deviceIDFromToken, currentTimeMillis, refreshTokenKey);
           }

           // 如果 accessToken 无效，尝试从 Redis 中获取对应的 refreshToken
           String refreshToken = stringRedisTemplate.opsForValue().get(refreshTokenKey);

           // 如果 Redis 中没有对应的 refreshToken，说明已过期或被删除
           if (refreshToken == null) {
               // 日志记录：refreshToken 未找到
               logger.warn("Refresh token not found in Redis for key: {}", refreshTokenKey);

               // 返回响应：刷新 token 失败，因为 refreshToken 已过期或无效
               return GeneralResponse.builder()
                       .code(HttpCode.FORBIDDEN)
                       .msg("Token无效，不要瞎搞哦～")
                       .data(null)
                       .build();
           }

           // 从 Redis 获取的 refreshToken 中提取时间戳
           Long timeStampFromRefreshToken = jwtUtil.getTimeStampFromToken(refreshToken);

           // 检查 refreshToken 和 accessToken 的时间戳是否相等
           if (timeStampFromRefreshToken.equals(timeStampFromToken)) {
               // 日志记录：时间戳一致，表示 refreshToken 有效
               logger.info("Refresh token is valid, generating new tokens.");

               // 调用方法生成新的 accessToken 和 refreshToken 并存储到 Redis
               return generateAndStoreNewTokens(username, deviceIDFromToken, currentTimeMillis, refreshTokenKey);
           } else {
               // 日志记录：时间戳不一致，说明 refreshToken 已经过期或无效
               logger.warn("Refresh token has expired or is invalid.");

               // 返回响应：刷新 token 失败，因为 refreshToken 无效
               return GeneralResponse.builder()
                       .code(HttpCode.FORBIDDEN)
                       .msg("refreshToken已过期，不要瞎搞哦～")
                       .data(null)
                       .build();
           }
       }catch (Exception e){
           logger.error("Failed to extract information from accessToken", e);
           return GeneralResponse.builder()
                   .code(HttpCode.BAD_REQUEST)
                   .msg("Invalid access token")
                   .data(null)
                   .build();
       }
    }

    /**
     * 生成新的 accessToken 和 refreshToken，并将 refreshToken 存储到 Redis 中
     *
     * @param username 用户名，从 accessToken 中提取
     * @param deviceIDFromToken 设备ID，从 accessToken 中提取
     * @param currentTimeMillis 当前时间戳，用于生成新 token
     * @param refreshTokenKey Redis 中存储 refreshToken 的键值
     * @return 返回 GeneralResponse，其中包含新的 accessToken
     */
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
            User user = userMapper.findUserByUsername(usernameCheckRequest.getUsername());
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
        String key = REFRESH_TOKEN_PREFIX + username+"-"+deviceId;
        stringRedisTemplate.delete(key);
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(USER_LOGGED_OUT)
                .data(null)
                .build();
    }

    @Override
    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        try {
            return userMapper.findUserByUsername(username);
        } catch (DataAccessException dae) {
            logger.error("Database error fetching user: {}", username, dae);
            throw new MyBatisSystemException(dae); // 转换为MyBatis特定异常
        }
    }

    // 缓存 refresh token 到 Redis
    private void cacheRefreshToken(String username, String deviceId,String refreshToken) {
        logger.info("Caching refresh token for user {}", username);
        String key = REFRESH_TOKEN_PREFIX + username+"-"+deviceId;
        stringRedisTemplate.opsForValue().set(key, refreshToken);
        stringRedisTemplate.expire(key,30L, TimeUnit.DAYS);
    }
}
