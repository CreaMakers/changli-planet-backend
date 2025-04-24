package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.creamakers.usersystem.consts.Config;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.*;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.PasswordEncoderUtil;
import com.creamakers.usersystem.util.RedisUtil;
import com.creamakers.usersystem.util.TencentCloudEmailUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TencentCloudEmailUtil tencentCloudEmailUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        logger.info("Attempting to register user: '{}'", username);

        String email = registerRequest.getEmail();
        String verificationCode = registerRequest.getVerifyCode();
        logger.info("Registration email: {}", email);

        // 验证邮箱格式
        if (!isValidEmail(email)) {
            return conflictResponse(ErrorMessage.EMAIL_FORMAT_INCORRECT);
        }

        try {
            // 检查邮箱是否已被绑定 - 移到try块内
            User existingUserWithEmail = userService.getUserByEmail(email);
            if (existingUserWithEmail != null) {
                logger.warn("Email '{}' is already registered to user '{}'", email, existingUserWithEmail.getUsername());
                return conflictResponse(ErrorMessage.EMAIL_ALREADY_REGISTERED);
            }

            if (userExists(username)) {
                logger.warn("User '{}' already exists, registration attempt aborted.", username);
                return conflictResponse(ErrorMessage.USER_ALREADY_EXISTS);
            }

            // 验证码校验
            if (verificationCode == null || verificationCode.isEmpty()) {
                logger.warn("Verification code is missing for user: '{}'", username);
                return badRequest(ErrorMessage.VERIFICATION_CODE_REQUIRED);
            }

            // 验证验证码是否正确
            if (!tencentCloudEmailUtil.verifyCode(email, verificationCode, Config.EMAIL_TYPE_REGISTER)) {
                logger.warn("Invalid verification code for user: '{}', email: '{}'", username, email);
                return badRequest(ErrorMessage.VERIFICATION_CODE_INVALID);
            }

            User newUser = createUserAndInsert(registerRequest);
            initializeUserProfileAndStats(newUser.getUserId(), username);
            logger.info("User '{}' successfully registered with UserId '{}'.", username, newUser.getUserId());
            return successResponse(SuccessMessage.USER_REGISTERED);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity error during registration for user '{}' with email '{}': {}",
                    username, email, e.getMessage(), e);
            return logAndRespondError("Data integrity error during registration: ", username, e, ErrorMessage.EMAIL_ALREADY_REGISTERED);
        } catch (DataAccessException e) {
            logger.error("Database error occurred while registering user '{}': {}", username, e.getMessage(), e);
            return logAndRespondError("Database error during registration for user: ", username, e, ErrorMessage.DATABASE_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while registering user '{}': {}", username, e.getMessage(), e);
            return logAndRespondError("Unexpected error during registration for user: ", username, e, ErrorMessage.OPERATION_FAILED);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> checkUsernameAvailability(UsernameCheckRequest request) {
        String username = request.getUsername();

        try {
            return userService.getUserByUsername(username) == null ?
                    successResponse(SuccessMessage.USER_NOT_EXITS) :
                    conflictResponse(ErrorMessage.USER_ALREADY_EXISTS);
        } catch (DataAccessException e) {
            return logAndRespondError("Database error during username check: ", username, e, ErrorMessage.DATABASE_ERROR);
        }
    }


    private boolean userExists(String username) {
        return userService.getUserByUsername(username) != null;
    }

    private User createUserAndInsert(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoderUtil.encodePassword(registerRequest.getPassword());
        return userService.createUserAndInsert(registerRequest, encodedPassword);
    }

    private void initializeUserProfileAndStats(Integer userId, String username) {
        userProfileService.initializeUserProfile(userId, username);
        userStatsService.initializeUserStats(userId, username);
    }

    private static boolean isValidEmail(String email) {
        logger.info("Validating email format for: {}", email);  // Log the email being validated
        if (StringUtil.isNullOrEmpty(email)) {
            logger.info("Email is null or empty.");  // Log if the email is null or empty
            return false;
        }
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        boolean isValid = email.matches(emailPattern);
        if (isValid) {
            logger.info("Email {} is valid.", email);  // Log if the email is valid
        } else {
            logger.info("Email {} is invalid.", email);  // Log if the email is invalid
        }
        return isValid;
    }


    @Override
    public ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId, String accessToken) {

        if (deviceId == null || deviceId.isEmpty()) {
            return badRequest("Missing device ID");
        }

        if (isValidAccessToken(accessToken, deviceId)) {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            return loginAndGenerateTokens(username, deviceId);
        }

        if (loginRequest != null) {
            return processPasswordLogin(loginRequest, deviceId);
        }

        logger.error("Login failed: Both access token and login request are missing for device '{}'", deviceId);
        return badRequest("Err:Both access token and login request are missing");
    }

    @Override
    public ResponseEntity<GeneralResponse> loginByEmail(LoginByEmailRequest loginByEmailRequest, String deviceId, String accessToken) {
        if (deviceId == null || deviceId.isEmpty()) {
            return badRequest("Missing device ID");
        }

        if (isValidAccessToken(accessToken, deviceId)) {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            return loginAndGenerateTokens(username, deviceId);
        }

        if (loginByEmailRequest != null) {
            return processEmailLogin(loginByEmailRequest, deviceId);
        }

        logger.error("both email and login request are missing for device '{}'", deviceId);
        return badRequest("Err:both email and login request are missing");
    }

    private ResponseEntity<GeneralResponse> processEmailLogin(LoginByEmailRequest loginByEmailRequest, String deviceId) {
        String email = loginByEmailRequest.getEmail();
        String verificationCode = loginByEmailRequest.getVerifyCode();

        // 验证参数
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(verificationCode)) {
            logger.warn("Missing email or verification code for email login");
            return badRequest(ErrorMessage.MISSING_CREDENTIALS);
        }

        try {
            // 验证验证码
            if (!tencentCloudEmailUtil.verifyCode(email, verificationCode, Config.EMAIL_TYPE_LOGIN)) {
                logger.warn("Invalid verification code for email login: {}", email);
                return unauthorized(ErrorMessage.VERIFICATION_CODE_INVALID);
            }

            // 根据邮箱获取用户
            User user = userService.getUserByEmail(email);

            // 验证用户存在性
            if (user == null) {
                logger.warn("Login attempt with non-existent email: {}", email);
                return notFound(ErrorMessage.USER_NOT_FOUND);
            }

            // 重用现有的登录和生成令牌方法
            logger.info("User '{}' successfully verified by email, proceeding with login on device '{}'",
                    user.getUsername(), deviceId);
            return loginAndGenerateTokens(user.getUsername(), deviceId);

        } catch (DataIntegrityViolationException e) {
            // 确保这里能捕获到上面抛出的异常
            logger.error("Caught DataIntegrityViolationException during email login: {}", e.getMessage(), e);
            return logAndRespondError("Data integrity error during login: ", email, e, ErrorMessage.EMAIL_ALREADY_REGISTERED);
        }catch (DataAccessException e) {
            return logAndRespondError("Database error during email login for email: ", email, e, ErrorMessage.DATABASE_ERROR);
        } catch (Exception e) {
            return logAndRespondError("Unexpected error during email login for email: ", email, e, ErrorMessage.LOGIN_FAILED);
        }
    }


    private boolean isValidAccessToken(String accessToken, String deviceId) {
        // Check if the access token is null or is blacklisted
        if (accessToken == null || userService.getCachedAccessTokenFromBlack(accessToken) != null) {
            logger.warn("Access token is invalid or blacklisted.");
            return false;
        }

        // Extract the username from the token
        String username = jwtUtil.getUserNameFromToken(accessToken);

        // Check if the user exists
        if (userService.getUserByUsername(username) == null) {
            logger.error("Login failed: User '{}' not found.", username);
            return false;
        }

        // Extract the device ID from the token
        String tokenDeviceId = jwtUtil.getDeviceIDFromToken(accessToken);

        // Check if the device ID matches and the refresh token is not expired
        if (!deviceId.equals(tokenDeviceId)) {
            logger.warn("Device ID mismatch: Expected '{}', but found '{}'.", deviceId, tokenDeviceId);
            return false;
        }

        if (userService.isRefreshTokenExpired(username, deviceId)) {
            logger.warn("Refresh token expired for user '{}' on device '{}'.", username, deviceId);
            return false;
        }

        // If all checks pass, return true
        logger.info("Access token for user '{}' on device '{}' is valid.", username, deviceId);
        return true;
    }


    private ResponseEntity<GeneralResponse> processPasswordLogin(LoginRequest loginRequest, String deviceId) {
        String username = loginRequest.getUsername();

        logger.info("Attempting password login for user '{}' on device '{}'", username, deviceId);

        User foundUser = userService.getUserByUsername(username);

        if (foundUser == null) {
            logger.warn("User '{}' not found for login attempt on device '{}'", username, deviceId);
            return notFound(ErrorMessage.USER_NOT_FOUND);
        }

        if (passwordEncoderUtil.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            logger.info("User '{}' successfully logged in on device '{}'", username, deviceId);
            return loginAndGenerateTokens(username, deviceId);
        }

        logger.warn("Invalid password for user '{}' on device '{}'", username, deviceId);
        return unauthorized(ErrorMessage.INVALID_PASSWORD);
    }


    private ResponseEntity<GeneralResponse> loginAndGenerateTokens(String username, String deviceId) {
        logger.info("Handling login for user '{}' with device '{}'", username, deviceId);

        long timestamp = System.currentTimeMillis();
        String newAccessToken = null;
        String newRefreshToken = null;

        try {
            newAccessToken = jwtUtil.generateAccessToken(username, deviceId, timestamp);
            newRefreshToken = jwtUtil.generateRefreshToken(username, deviceId, timestamp);

            userService.cacheRefreshToken(username, deviceId, newRefreshToken);

            logger.debug("Generated new tokens for user '{}': AccessToken (hidden), RefreshToken (hidden), Timestamp: {}", username, timestamp);
            logger.info("User '{}' logged in successfully on device '{}'. Tokens generated and cached.", username, deviceId);
        } catch (Exception e) {
            logger.error("Error occurred during login process for user '{}' on device '{}': {}", username, deviceId, e.getMessage(), e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, ErrorMessage.TOKEN_GENERATION_FAILED);
        }

        return responseWithAuthHeader(newAccessToken, HttpStatus.OK, HttpCode.OK, SuccessMessage.USER_LOGGED_IN);
    }


    @Override
    public ResponseEntity<GeneralResponse> updatePassword(PasswordUpdateRequest request, String accessToken) {
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            return badRequest(ErrorMessage.PASSWORD_NOT_SAME);
        }

        String username = jwtUtil.getUserNameFromToken(accessToken);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoderUtil.encodePassword(request.getNewPassword()));

        return userService.updateUserPassword(user) ?
                successResponse(SuccessMessage.USER_UPDATED) :
                notFound(ErrorMessage.USER_NOT_FOUND);
    }

    @Override
    public ResponseEntity<GeneralResponse> updateUsername(UsernameUpdateRequest request, String accessToken) {
        return userService.updateUserUsername(request) ?
                successResponse(SuccessMessage.USER_UPDATED) :
                notFound(ErrorMessage.USER_NOT_FOUND);
    }

    @Override
    public ResponseEntity<GeneralResponse> registerVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        String email = verificationCodeRequest.getEmail();
        if (!isValidEmail(email)) {
            logger.warn("Invalid email format: {}", email);
            return badRequest(ErrorMessage.EMAIL_FORMAT_INCORRECT);
        }

        try {
            // 调用发送验证码方法
            String verificationCode = tencentCloudEmailUtil.sendVerificationCodeEmail(email, Config.EMAIL_TYPE_REGISTER);

            // 如果发送成功，返回成功响应
            if (verificationCode != null) {
                logger.info("Verification code sent successfully to: {}", email);
                return successResponse(SuccessMessage.VERIFICATION_CODE_SENT);
            } else {
                // 如果发送失败但没有抛出异常，返回错误响应
                logger.error("Failed to send verification code to: {}", email);
                return badRequest(ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("请勿频繁发送验证码")) {
                logger.warn("Rate limiting applied to verification code request for email: {}", email);
                return conflictResponse(ErrorMessage.VERIFICATION_CODE_TOO_FREQUENT);
            } else if (e.getMessage() != null && e.getMessage().contains("验证码请求次数已达上限")) {
                logger.warn("Daily limit reached for verification code requests for email: {}", email);
                return conflictResponse(ErrorMessage.VERIFICATION_CODE_DAILY_LIMIT_REACHED);
            } else if (e.getMessage() != null && e.getMessage().contains("无效的验证码类型")) {
                logger.error("Invalid verification code type for email: {}", email);
                return badRequest(ErrorMessage.INVALID_VERIFICATION_CODE_TYPE);
            } else {
                // 其他运行时异常
                return logAndRespondError("Error sending verification code to email:", email, e, ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
            }
        } catch (Exception e) {
            // 如果发送过程中发生异常，记录日志并返回错误响应
            return logAndRespondError("Error sending verification code to email:", email, e, ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> loginVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        String email = verificationCodeRequest.getEmail();
        if (!isValidEmail(email)) {
            logger.warn("Invalid email format: {}", email);
            return badRequest(ErrorMessage.EMAIL_FORMAT_INCORRECT);
        }

        try {
            // 调用发送验证码方法，指定为登录验证码类型
            String verificationCode = tencentCloudEmailUtil.sendVerificationCodeEmail(email, Config.EMAIL_TYPE_LOGIN);

            // 如果发送成功，返回成功响应
            if (verificationCode != null) {
                logger.info("Login verification code sent successfully to: {}", email);
                return successResponse(SuccessMessage.VERIFICATION_CODE_SENT);
            } else {
                // 如果发送失败但没有抛出异常，返回错误响应
                logger.error("Failed to send login verification code to: {}", email);
                return badRequest(ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("请勿频繁发送验证码")) {
                logger.warn("Rate limiting applied to verification code request for email: {}", email);
                return conflictResponse(ErrorMessage.VERIFICATION_CODE_TOO_FREQUENT);
            } else if (e.getMessage() != null && e.getMessage().contains("验证码请求次数已达上限")) {
                logger.warn("Daily limit reached for verification code requests for email: {}", email);
                return conflictResponse(ErrorMessage.VERIFICATION_CODE_DAILY_LIMIT_REACHED);
            } else if (e.getMessage() != null && e.getMessage().contains("无效的验证码类型")) {
                logger.error("Invalid verification code type for email: {}", email);
                return badRequest(ErrorMessage.INVALID_VERIFICATION_CODE_TYPE);
            } else {
                // 其他运行时异常
                return logAndRespondError("Error sending verification code to email:", email, e, ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
            }
        } catch (Exception e) {
            // 如果发送过程中发生其他异常，记录日志并返回错误响应
            return logAndRespondError("Error sending login verification code to email:", email, e, ErrorMessage.VERIFICATION_CODE_SEND_FAILED);
        }
    }


    @Override
    public ResponseEntity<GeneralResponse> quit(String accessToken, String deviceId) {
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);

            logger.info("User logout attempt. AccessToken: {}, DeviceId: {}", accessToken, deviceId);

            if (username == null) {
                logger.warn("Failed logout attempt. Username could not be extracted from access token.");
                return unauthorized(ErrorMessage.UNAUTHORIZED_ACCESS);
            }

            logger.info("Username: {} is logging out from device: {}", username, deviceId);

            userService.deleteRefreshToken(username, deviceId);
            logger.info("Refresh token deleted for username: {} on device: {}", username, deviceId);

            userService.addAccessToBlacklist(accessToken);
            logger.info("Access token added to blacklist for username: {} on device: {}", username, deviceId);


            logger.info("User: {} successfully logged out from device: {}", username, deviceId);
            return successResponse(SuccessMessage.USER_LOGGED_OUT);
        } catch (Exception e) {
            // 异常捕获，记录详细错误信息
            logger.error("An error occurred during logout process for accessToken: {}, deviceId: {}. Error: {}", accessToken, deviceId, e.getMessage());
            return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, "Internal server error.");
        }
    }

    @Override
    public ResponseEntity<GeneralResponse> refreshAuth(String auth) {
        try {
            if (!jwtUtil.validateToken(auth)) {
                return unauthorized(ErrorMessage.INVALID_TOKEN);
            }

            String username = jwtUtil.getUserNameFromToken(auth);
            String deviceId = jwtUtil.getDeviceIDFromToken(auth);

            logger.info("Refreshing auth for user '{}' with device '{}'", username, deviceId);

            logger.info("Checking if the refresh token for user '{}' on device '{}' is expired.", username, deviceId);
            if (userService.isRefreshTokenExpired(username, deviceId)) {
                logger.warn("Refresh token for user '{}' on device '{}' is expired.", username, deviceId);
                return unauthorized(ErrorMessage.TOKEN_EXPIRED);
            }

            // Generate new access token
            long timestamp = System.currentTimeMillis();
            String newAccessToken = jwtUtil.generateAccessToken(username, deviceId, timestamp);

            logger.debug("Generated new tokens for user '{}': AccessToken (hidden), Timestamp: {}", username, timestamp);

            redisUtil.refreshTokenIfNeeded(username, deviceId);

            return responseWithAuthHeader(
                    newAccessToken,
                    HttpStatus.OK,
                    HttpCode.OK,
                    SuccessMessage.TOKEN_REFRESHED
            );
        } catch (Exception e) {
            return logAndRespondError("Error refreshing token: ", e, ErrorMessage.OPERATION_FAILED);
        }
    }

    private ResponseEntity<GeneralResponse> conflictResponse(String message) {
        return response(HttpStatus.CONFLICT, HttpCode.CONFLICT, message);
    }

    private ResponseEntity<GeneralResponse> successResponse(String message) {
        return response(HttpStatus.OK, HttpCode.OK, message);
    }

    private ResponseEntity<GeneralResponse> notFound(String message) {
        return response(HttpStatus.NOT_FOUND, HttpCode.NOT_FOUND, message);
    }

    private ResponseEntity<GeneralResponse> unauthorized(String message) {
        return response(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, message);
    }

    private ResponseEntity<GeneralResponse> badRequest(String message) {
        return response(HttpStatus.BAD_REQUEST, HttpCode.BAD_REQUEST, message);
    }

    private ResponseEntity<GeneralResponse> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new GeneralResponse(code, message, null));
    }

    private ResponseEntity<GeneralResponse> responseWithAuthHeader(String accessToken, HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).header("Authorization", "Bearer " + accessToken)
                .body(new GeneralResponse(code, message, null));
    }

    private ResponseEntity<GeneralResponse> logAndRespondError(String logMessage, String identifier, Exception e, String errorMsg) {
        logger.error("{} {} - {}", logMessage, identifier, e.getMessage(), e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, errorMsg);
    }

    private ResponseEntity<GeneralResponse> logAndRespondError(String logMessage, Exception e, String errorMsg) {
        logger.error("{} - {}", logMessage, e.getMessage(), e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpCode.INTERNAL_SERVER_ERROR, errorMsg);
    }

    private ResponseEntity<GeneralResponse> logAndRespondConflict(String logMessage, String identifier, Exception e, String errorMsg) {
        logger.warn("{} {}", logMessage, identifier, e);
        return conflictResponse(errorMsg);
    }

}
