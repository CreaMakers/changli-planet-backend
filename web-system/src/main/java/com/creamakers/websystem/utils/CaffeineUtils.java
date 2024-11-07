package com.creamakers.websystem.utils;

import com.creamakers.websystem.constants.CommonConst;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.TimeUnit;
@Component
public class CaffeineUtils {
    @Autowired
    private RedisUtil redisUtil;
    public static void main(String[] args) {
        JwtUtils jwtUtils = new JwtUtils();
        System.out.println(jwtUtils.getUserNameOrNull("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVUaW1lIjoxNzMwOTg1NTE5LCJleHAiOjE3MzA5OTI3MTksInVzZXJuYW1lIjoi5p2O5ZubIn0.eG0wxqTth4mGKEc9TmX7RDjWl9hIu-rW47cwBnUY_ig"));
    }
}
