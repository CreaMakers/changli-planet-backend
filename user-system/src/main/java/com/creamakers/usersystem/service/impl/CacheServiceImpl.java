package com.creamakers.usersystem.service.impl;

import com.creamakers.usersystem.consts.Constants;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void cacheUser(User user) {
        if (user != null && user.getUsername() != null) {
            String key = Constants.USER_CACHE_PREFIX + user.getUsername();
            redisTemplate.opsForValue().set(key, user, Constants.USER_CACHE_TIMEOUT, TimeUnit.HOURS);
        }
    }

    @Override
    public User getCachedUser(String username) {
        if (username != null) {
            String key = Constants.USER_CACHE_PREFIX + username;
            return (User) redisTemplate.opsForValue().get(key);
        }
        return null;
    }
}
