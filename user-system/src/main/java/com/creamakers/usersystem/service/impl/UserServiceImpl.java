package com.creamakers.usersystem.service.impl;

import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.response.LoginSuccessData;

import com.creamakers.usersystem.exception.UserServiceException;
import com.creamakers.usersystem.mapper.UserMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.RedisUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    @Override
    public GeneralResponse login(LoginRequest loginRequest) {
        logger.info("Attempting to log in user: {}", loginRequest.getUsername());
        try {
            User user = getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                logger.warn("Login attempt failed: User not found");
                return new GeneralResponse(HttpCode.NOT_FOUND, ErrorMessage.USER_NOT_FOUND, null);
            }

            // 使用 BCrypt 验证加密密码
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String accessToken = jwtUtil.generateToken(user.getUsername());
                String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

                cacheRefreshToken(user.getUsername(), refreshToken);

                LoginSuccessData successData = LoginSuccessData.builder()
                        .access_token(accessToken)
                        .refresh_token(refreshToken)
                        .expires_in("3600")
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
                    .build();

            int rowsAffected = userMapper.insertUser(newUser);
            if (rowsAffected > 0) {
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
    public GeneralResponse refreshAuth(String refreshToken, String accessToken) {
        logger.info("Attempting to refresh token for accessToken: {}", accessToken);
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            String cachedRefreshToken = (String) redisUtil.getValue(REFRESH_TOKEN_PREFIX + username);

            if (cachedRefreshToken != null && cachedRefreshToken.equals(refreshToken)) {
                String newAccessToken = jwtUtil.generateToken(username);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);
                cacheRefreshToken(username, newRefreshToken);

                LoginSuccessData successData = LoginSuccessData.builder()
                        .access_token(newAccessToken)
                        .refresh_token(newRefreshToken)
                        .expires_in("3600")
                        .build();

                logger.info("Token refreshed successfully for user {}", username);
                return GeneralResponse.builder()
                        .code(HttpCode.OK)
                        .msg(SuccessMessage.OPERATION_SUCCESSFUL)
                        .data(successData)
                        .build();
            } else {
                logger.warn("Invalid refresh token provided for user {}", username);
                return new GeneralResponse(HttpCode.UNAUTHORIZED, ErrorMessage.INVALID_CREDENTIALS, null);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.OPERATION_FAILED)
                    .data(null)
                    .build();
        }
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
    public GeneralResponse quit(String refreshToken, String accessToken) {
        logger.info("User logout process initiated");
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            redisUtil.deleteValue(REFRESH_TOKEN_PREFIX + username);
            logger.info("User {} logged out successfully", username);
            return GeneralResponse.builder()
                    .code(HttpCode.OK)
                    .msg(SuccessMessage.USER_LOGGED_OUT)
                    .data(null)
                    .build();
        } catch (Exception e) {
            logger.error("Error during logout process: {}", e.getMessage(), e);
            return GeneralResponse.builder()
                    .code(HttpCode.INTERNAL_SERVER_ERROR)
                    .msg(ErrorMessage.OPERATION_FAILED)
                    .data(null)
                    .build();
        }
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
    private void cacheRefreshToken(String username, String refreshToken) {
        logger.info("Caching refresh token for user {}", username);
        redisUtil.setValue(REFRESH_TOKEN_PREFIX + username, refreshToken, 30, TimeUnit.DAYS);
    }
}
