package com.creamakers.usersystem.service;

import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.request.PasswordUpdateRequest;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/**
 * @author yuxialuozi
 * @data 2024/10/29 - 17:19
 * @description
 */
public interface UserAuthService {

    ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId, HttpServletResponse response);

    ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest);

    GeneralResponse checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest);

    GeneralResponse quit(String accessToken);

    GeneralResponse refreshAuth(String accessToken);

    GeneralResponse updatePassword(PasswordUpdateRequest request, String accessToken);

}
