package com.creamakers.usersystem.util;

import com.creamakers.usersystem.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String refreshTokenPrefix;

    @Value("${BLACKLIST_TOKEN_PREFIX}")
    private String blackListTokenPrefix;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION_TIME}")
    private Long jwtRefreshTokenExpirationTime;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void storeRefreshToken(String username, String deviceId, String refreshToken) {
        try {
            String key = refreshTokenPrefix + username + "-" + deviceId;
            redisTemplate.opsForValue().set(key, refreshToken, jwtRefreshTokenExpirationTime, TimeUnit.MILLISECONDS);
            logger.info("Stored refresh token for user {} on device {} with key {}", username, deviceId, key);
        } catch (Exception e) {
            logger.error("Error storing refresh token for user {} on device {}: {}", username, deviceId, e.getMessage(), e);
        }
    }

    public boolean isRefreshTokenExpired(String username, String deviceId) {
        try {
            String key = refreshTokenPrefix + username + "-" + deviceId;
            Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);

            if (ttl == null || ttl <= 0) {
                logger.info("Token expired or not found for user {} on device {}", username, deviceId);
                return true;
            }

            logger.info("Checked expiration for user {} on device {}: false", username, deviceId);
            return false;
        } catch (Exception e) {
            logger.error("Error checking expiration for user {} on device {} ", username, deviceId, e);
        }
        return false;
    }


    public void deleteRefreshToken(String username, String deviceId) {
        try {
            String key = refreshTokenPrefix + username + "-" + deviceId;
            redisTemplate.delete(key);
            logger.info("Deleted refresh token for user {} on device {} with key {}", username, deviceId, key);
        } catch (Exception e) {
            logger.error("Error deleting refresh token for user {} on device {}: {}", username, deviceId, e.getMessage(), e);
        }
    }

    public boolean refreshTokenExists(String username, String deviceId) {
        try {
            String key = refreshTokenPrefix + username + "-" + deviceId;
            boolean exists = redisTemplate.hasKey(key);
            logger.info("Checked existence of refresh token for user {} on device {}: {}", username, deviceId, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking existence of refresh token for user {} on device {}: {}", username, deviceId, e.getMessage(), e);
            return false;
        }
    }

    public void addAccessToBlacklist(String accessToken) {
        try {
            String key = blackListTokenPrefix + accessToken;
            redisTemplate.opsForSet().add(key, accessToken);
            logger.info("Added access token to blacklist: {}", accessToken);
        } catch (Exception e) {
            logger.error("Error adding access token to blacklist: {}", accessToken, e);
        }
    }

    public String getCachedAccessTokenFromBlack(String accessToken) {
        try {
            String key = blackListTokenPrefix + accessToken;
            Set<String> tokens = redisTemplate.opsForSet().members(key);
            String cachedToken = (tokens != null && !tokens.isEmpty()) ? tokens.iterator().next() : null;
            logger.info("Retrieved cached access token from blacklist for token {}: {}", accessToken, cachedToken);
            return cachedToken;
        } catch (Exception e) {
            logger.error("Error retrieving cached access token from blacklist for token {}: {}", accessToken, e.getMessage(), e);
            return null;
        }
    }

    public String getFreshTokenByUsernameAndDeviceId(String username, String deviceId) {
        try {
            String key = refreshTokenPrefix + username + "-" + deviceId;
            String refreshToken = redisTemplate.opsForValue().get(key);
            if (refreshToken != null) {
                logger.info("Successfully retrieved refresh token for user {} on device {}", username, deviceId);
            } else {
                logger.warn("No refresh token found for user {} on device {}", username, deviceId);
            }
            return refreshToken;
        } catch (Exception e) {
            logger.error("Error retrieving refresh token for user {} on device {}: {}", username, deviceId, e.getMessage(), e);
            return null;
        }
    }

    public void refreshTokenIfNeeded(String username, String deviceId) {
        try {
            String existingRefreshToken = getFreshTokenByUsernameAndDeviceId(username, deviceId);
            if (existingRefreshToken != null) {
                Long ttl = redisTemplate.getExpire(refreshTokenPrefix + username + "-" + deviceId, TimeUnit.SECONDS);
                if (ttl != null && ttl < TimeUnit.DAYS.toSeconds(10)) {
                    storeRefreshToken(username, deviceId, existingRefreshToken);
                    logger.info("Refresh token for user '{}' on device '{}' has been refreshed due to less than 10 days left.", username, deviceId);
                } else {
                    logger.info("No need to refresh the refresh token for user '{}' on device '{}', remaining time is sufficient.", username, deviceId);
                }
            }
        } catch (Exception e) {
            logger.error("Error while refreshing the refresh token for user '{}' on device '{}': {}", username, deviceId, e.getMessage(), e);
        }
    }

}
