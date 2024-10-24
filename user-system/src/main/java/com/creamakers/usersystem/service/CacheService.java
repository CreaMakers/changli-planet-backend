package com.creamakers.usersystem.service;

import com.creamakers.usersystem.po.User;

/**
 * @author yuxialuozi
 * @data 2024/10/22 - 23:26
 * @description
 */

public interface CacheService {
    void cacheUser(User user);

    User getCachedUser(String username);
}
