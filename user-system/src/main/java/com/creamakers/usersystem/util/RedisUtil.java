package com.creamakers.usersystem.util;

import com.creamakers.usersystem.consts.Config;
import com.creamakers.usersystem.consts.RedisKeyConst;
import com.creamakers.usersystem.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.creamakers.usersystem.consts.Config.EMAIL_TYPE_LOGIN;
import static com.creamakers.usersystem.consts.Config.EMAIL_TYPE_REGISTER;
import static com.creamakers.usersystem.consts.RedisKeyConst.*;

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

    // 验证码发送频率限制检查（60秒内不能重复发送）
    public boolean canSendVerificationCode(String email) {
        try {
            String throttleKey = EMAIL_THROTTLE_PREFIX + email;
            Boolean canSend = redisTemplate.opsForValue().setIfAbsent(throttleKey, "1", 60, TimeUnit.SECONDS);

            if (canSend == null || !canSend) {
                logger.info("Rate limit applied for verification code to email: {}", email);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Error checking rate limit for email {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查邮箱验证码每日发送限制
     *
     * @param email 目标邮箱
     * @param maxDailyAttempts 每日最大尝试次数
     * @param emailType 邮件类型
     * @return 是否未超过限制
     */
    public boolean checkDailyVerificationLimit(String email, int maxDailyAttempts, String emailType) {
        try {
            // 获取对应类型的每日限制前缀
            String prefix = getDailyLimitPrefix(emailType);
            String dailyLimitKey = RedisKeyConst.getKey(prefix, email);

            // 获取当前计数，如果不存在则创建并返回1
            Long currentCount = redisTemplate.opsForValue().increment(dailyLimitKey, 1);

            // 如果是新创建的计数器，设置24小时过期时间
            if (currentCount != null && currentCount.equals(1L)) {
                redisTemplate.expire(dailyLimitKey, 24, TimeUnit.HOURS);
                logger.info("Created new daily limit counter for email: {} with type: {}", email, emailType);
            }

            // 检查是否超过限制
            if (currentCount != null && currentCount > maxDailyAttempts) {
                logger.warn("Email {} has exceeded the daily {} verification code limit of {}",
                        email, emailType, maxDailyAttempts);
                return false;
            }

            logger.info("Email {} has used {} of {} daily {} verification code attempts",
                    email, currentCount, maxDailyAttempts, emailType);
            return true;
        } catch (Exception e) {
            logger.error("Error checking daily verification limit for email {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取对应邮件类型的每日限制前缀
     *
     * @param emailType 邮件类型
     * @return Redis键前缀
     */
    private String getDailyLimitPrefix(String emailType) {
        switch (emailType) {
            case Config.EMAIL_TYPE_LOGIN:
                return RedisKeyConst.LOGIN_DAILY_LIMIT_PREFIX;
            case Config.EMAIL_TYPE_REGISTER:
                return RedisKeyConst.REGISTER_DAILY_LIMIT_PREFIX;
            case Config.EMAIL_TYPE_UPDATE_EMAIL:
                return RedisKeyConst.UPDATE_EMAIL_DAILY_LIMIT_PREFIX;
            case Config.EMAIL_TYPE_RESET_PASSWORD:
                return RedisKeyConst.RESET_PASSWORD_DAILY_LIMIT_PREFIX;
            default:
                logger.error("Invalid email type: {}", emailType);
                throw new IllegalArgumentException("无效的验证码类型: " + emailType);
        }
    }
    // 删除频率限制标记
    public void removeRateLimit(String email) {
        try {
            String throttleKey = EMAIL_THROTTLE_PREFIX + email;
            redisTemplate.delete(throttleKey);
            logger.info("Removed rate limit for email: {}", email);
        } catch (Exception e) {
            logger.error("Error removing rate limit for email {}: {}", email, e.getMessage(), e);
        }
    }

    /**
     * 存储登录验证码
     *
     * @param email 目标邮箱
     * @param verificationCode 验证码
     */
    public void storeLoginVerificationCode(String email, String verificationCode) {
        try {
            String key = RedisKeyConst.getKey(RedisKeyConst.LOGIN_VERIFICATION_CODE_PREFIX, email);
            redisTemplate.opsForValue().set(key, verificationCode,
                    RedisKeyConst.VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            logger.info("Stored login verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error storing login verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    /**
     * 存储更新邮箱验证码
     *
     * @param email 目标邮箱
     * @param verificationCode 验证码
     */
    public void storeUpdateEmailVerificationCode(String email, String verificationCode) {
        try {
            String key = RedisKeyConst.getKey(RedisKeyConst.UPDATE_EMAIL_VERIFICATION_CODE_PREFIX, email);
            redisTemplate.opsForValue().set(key, verificationCode,
                    RedisKeyConst.VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            logger.info("Stored update email verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error storing update email verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    /**
     * 存储重置密码验证码
     *
     * @param email 目标邮箱
     * @param verificationCode 验证码
     */
    public void storeResetPasswordVerificationCode(String email, String verificationCode) {
        try {
            String key = RedisKeyConst.getKey(RedisKeyConst.RESET_PASSWORD_VERIFICATION_CODE_PREFIX, email);
            redisTemplate.opsForValue().set(key, verificationCode,
                    RedisKeyConst.VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            logger.info("Stored reset password verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error storing reset password verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    public String getLoginVerificationCode(String email) {
        try {
            String key = LOGIN_VERIFICATION_CODE_PREFIX + email;
            String verificationCode = redisTemplate.opsForValue().get(key);
            if (verificationCode != null) {
                logger.info("Successfully retrieved login verification code for user {}", email);
            } else {
                logger.warn("No login verification code found for user {}", email);
            }
            return verificationCode;
        } catch (Exception e) {
            logger.error("Error retrieving login verification code for user {}: {}", email, e.getMessage(), e);
            return null;
        }
    }

    public void deleteLoginVerificationCode(String email) {
        try {
            String key = LOGIN_VERIFICATION_CODE_PREFIX + email;
            redisTemplate.delete(key);
            logger.info("Deleted login verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error deleting login verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    // 注册验证码相关方法
    public void storeRegisterVerificationCode(String email, String verificationCode) {
        try {
            String key = REGISTER_VERIFICATION_CODE_PREFIX + email;
            redisTemplate.opsForValue().set(key, verificationCode, VERIFICATION_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            logger.info("Stored register verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error storing register verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    public String getRegisterVerificationCode(String email) {
        try {
            String key = REGISTER_VERIFICATION_CODE_PREFIX + email;
            String verificationCode = redisTemplate.opsForValue().get(key);
            if (verificationCode != null) {
                logger.info("Successfully retrieved register verification code for user {}", email);
            } else {
                logger.warn("No register verification code found for user {}", email);
            }
            return verificationCode;
        } catch (Exception e) {
            logger.error("Error retrieving register verification code for user {}: {}", email, e.getMessage(), e);
            return null;
        }
    }

    public void deleteRegisterVerificationCode(String email) {
        try {
            String key = REGISTER_VERIFICATION_CODE_PREFIX + email;
            redisTemplate.delete(key);
            logger.info("Deleted register verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error deleting register verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    public void deleteUpdateEmailVerificationCode(String email) {
        try {
            String key = UPDATE_EMAIL_VERIFICATION_CODE_PREFIX + email;
            redisTemplate.delete(key);
            logger.info("Deleted update email verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error deleting update email verification code for user {}: {}", email, e.getMessage(), e);
        }
    }

    public void deleteResetPasswordVerificationCode(String email) {
        try {
            String key = RESET_PASSWORD_VERIFICATION_CODE_PREFIX + email;
            redisTemplate.delete(key);
            logger.info("Deleted reset password verification code for user {} with key {}", email, key);
        } catch (Exception e) {
            logger.error("Error deleting reset password verification code for user {}: {}", email, e.getMessage(), e);
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
