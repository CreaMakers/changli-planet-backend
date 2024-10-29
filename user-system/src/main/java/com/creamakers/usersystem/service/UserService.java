package com.creamakers.usersystem.service;


import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    int addUser(User newUser);

    User getUserByUsername(String username);

    void cacheRefreshToken(String username, String deviceId, String refreshToken);
}
