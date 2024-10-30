package com.creamakers.usersystem.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String refreshTokenPrefix;

    @Value("${BLACKLIST_TOKEN_PREFIX}")
    private String blackListTokenPrefix;


    @Value("${JWT_REFRESH_TOKEN_EXPIRATION_TIME}")
    private Long jwtRefreshTokenExpirationTime;

    @Autowired
    private StringRedisTemplate redisTemplate;



    public void storeRefreshToken(String username,String deviceId, String refreshToken) {
        String key = refreshTokenPrefix + username + "-" + deviceId;
        redisTemplate.opsForValue().set(key, refreshToken, jwtRefreshTokenExpirationTime, TimeUnit.MILLISECONDS);
    }


    public String getRefreshToken(String username,String deviceId) {
        String key = refreshTokenPrefix + username + "-" + deviceId;
        return redisTemplate.opsForValue().get(key);
    }


    public void deleteRefreshToken(String username,String deviceId) {
        String key = refreshTokenPrefix + username + "-" + deviceId;
        redisTemplate.delete(key);
    }


    public boolean refreshTokenExists(String username,String deviceId) {
        String key = refreshTokenPrefix + username + "-" + deviceId;
        return redisTemplate.hasKey(key);
    }

    public void addAccessToBlacklist(String accessToken) {
        String key = blackListTokenPrefix + accessToken;
        redisTemplate.opsForSet().add(key, accessToken);
    }

}
