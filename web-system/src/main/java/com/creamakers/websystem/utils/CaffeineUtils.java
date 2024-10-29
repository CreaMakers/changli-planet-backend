package com.creamakers.websystem.utils;

import com.creamakers.websystem.constants.CommonConst;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    }
}
