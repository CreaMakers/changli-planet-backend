package com.creamakers.usersystem.service.impl;

import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.*;
import com.creamakers.usersystem.util.JwtUtil;
import com.creamakers.usersystem.util.PasswordEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        logger.info("Attempting to register user: {}", username);

        if (userExists(username)) {
            return conflictResponse(ErrorMessage.USER_ALREADY_EXISTS);
        }

        try {
            User newUser = createUserAndInsert(registerRequest);
            initializeUserProfileAndStats(newUser.getUserId());
            return successResponse(SuccessMessage.USER_REGISTERED);
        } catch (DuplicateKeyException e) {
            return logAndRespondConflict("Duplicate username found during registration: ", username, e, ErrorMessage.USER_ALREADY_EXISTS);
        } catch (DataAccessException e) {
            return logAndRespondError("Database error during registration for user: ", username, e, ErrorMessage.DATABASE_ERROR);
        } catch (Exception e) {
            return logAndRespondError("Unexpected error during registration for user: ", username, e, ErrorMessage.OPERATION_FAILED);
        }
    }

    private boolean userExists(String username) {
        return userService.getUserByUsername(username) != null;
    }

    private User createUserAndInsert(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoderUtil.encodePassword(registerRequest.getPassword());
        return userService.createUserAndInsert(registerRequest, encodedPassword);
    }

    private void initializeUserProfileAndStats(Integer userId) {
        userProfileService.initializeUserProfile(userId);
        userStatsService.initializeUserStats(userId);
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
            return response(HttpStatus.INTERNAL_SERVER_ERROR,HttpCode.INTERNAL_SERVER_ERROR , ErrorMessage.TOKEN_GENERATION_FAILED);
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

        return userService.updateUser(user) ?
                successResponse(SuccessMessage.USER_UPDATED) :
                notFound(ErrorMessage.USER_NOT_FOUND);
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

    @Override
    public ResponseEntity<GeneralResponse> quit(String accessToken, String deviceId) {
        String username = jwtUtil.getUserNameFromToken(accessToken);

        if (username == null) {
            return unauthorized(ErrorMessage.UNAUTHORIZED_ACCESS);
        }

        userService.deleteRefreshToken(username, deviceId);
        userService.addAccessToBlacklist(accessToken);
        return successResponse(SuccessMessage.USER_LOGGED_OUT);
    }

    @Override
    public ResponseEntity<GeneralResponse> refreshAuth(String auth) {
        try {
            if (!jwtUtil.validateToken(auth)) {
                return unauthorized(ErrorMessage.INVALID_TOKEN);
            }

            String username = jwtUtil.getUserNameFromToken(auth);
            String deviceId = jwtUtil.getDeviceIDFromToken(auth);

            if (userService.isRefreshTokenExpired(username, deviceId)) {
                return unauthorized(ErrorMessage.TOKEN_EXPIRED);
            }

            return responseWithAuthHeader(
                    jwtUtil.generateAccessToken(username, deviceId, System.currentTimeMillis()),
                    HttpStatus.OK, HttpCode.OK, SuccessMessage.TOKEN_REFRESHED
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
