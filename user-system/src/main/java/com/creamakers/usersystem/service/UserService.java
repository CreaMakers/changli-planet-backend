package com.creamakers.usersystem.service;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    User getUserByUsername(String username);


    void cacheRefreshToken(String username, String deviceId, String refreshToken);

    boolean isRefreshTokenExpired(String username, String deviceId);

    void deleteRefreshToken(String username, String deviceId);

    String getCachedAccessTokenFromBlack(String accessToken);

    User createUser(RegisterRequest registerRequest, String encodedPassword);

    boolean updateUser(User user);

    int saveUser(User newUser);

    void addAccessToBlacklist(String accessToken);

}
