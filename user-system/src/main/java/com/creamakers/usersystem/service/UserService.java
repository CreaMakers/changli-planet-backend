package com.creamakers.usersystem.service;


import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;

public interface UserService {
    GeneralResponse login(LoginRequest loginRequest);

    User getUserByUsername(String username); // 根据用户名获取用户

    GeneralResponse register(RegisterRequest registerRequest);

    GeneralResponse checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest);

    GeneralResponse quit(String accessToken);

    GeneralResponse refreshAuth(String accessToken);
}
