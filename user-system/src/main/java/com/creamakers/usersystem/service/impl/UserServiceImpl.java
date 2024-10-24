package com.creamakers.usersystem.service.impl;

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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    CacheServiceImpl cacheService;

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
        try {
            User user = getUserByUsername(loginRequest.getUsername());
            if (user == null) {
                return new GeneralResponse("404", "User not found", null);
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

                return GeneralResponse.builder()
                        .code("200")
                        .msg("Login successful")
                        .data(successData)
                        .build();
            } else {
                return new GeneralResponse("400", "Invalid username or password", null);
            }
        } catch (DataAccessException dae) {
            // 数据库访问异常处理
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Database error: " + dae.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            // 其他异常处理
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Login failed: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse register(RegisterRequest registerRequest) {
        try {
            if (userMapper.findUserByUsername(registerRequest.getUsername()) != null) {
                return new GeneralResponse("400", "Username already exists", null);
            }

            // 加密用户密码
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            User newUser = User.builder()
                    .username(registerRequest.getUsername())
                    .password(encodedPassword)
                    .build();

            int rowsAffected = userMapper.insertUser(newUser);
            if (rowsAffected > 0) {
                return GeneralResponse.builder()
                        .code("201")
                        .msg("User registered successfully")
                        .data(newUser.getUserId()) // 返回用户ID
                        .build();
            } else {
                throw new UserServiceException("Failed to register user", "500");
            }
        } catch (DataAccessException dae) {
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Database error: " + dae.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Registration failed: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse refreshAuth(String refreshToken, String accessToken) {
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

                return GeneralResponse.builder()
                        .code("200")
                        .msg("Token refreshed successfully")
                        .data(successData)
                        .build();
            } else {
                return new GeneralResponse("401", "Invalid refresh token", null);
            }
        } catch (Exception e) {
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Token refresh failed: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest) {
        try {
             User user = userMapper.findUserByUsername(usernameCheckRequest.getUsername());
            if (user != null) {
                return new GeneralResponse("409", "Username already taken", null);
            }
            return new GeneralResponse("200", "Username is available", null);
        } catch (DataAccessException dae) {
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Database error: " + dae.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public GeneralResponse quit(String refreshToken, String accessToken) {
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            redisUtil.deleteValue(REFRESH_TOKEN_PREFIX + username);

            return GeneralResponse.builder()
                    .code("200")
                    .msg("Logged out successfully")
                    .data(null)
                    .build();
        } catch (Exception e) {
            return GeneralResponse.builder()
                    .code("500")
                    .msg("Logout failed: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return userMapper.findUserByUsername(username);
        } catch (DataAccessException dae) {
            throw new MyBatisSystemException(dae); // 转换为MyBatis特定异常
        }
    }

    // 缓存 refresh token 到 Redis
    private void cacheRefreshToken(String username, String refreshToken) {
        redisUtil.setValue(REFRESH_TOKEN_PREFIX + username, refreshToken, 30, TimeUnit.DAYS);
    }
}
