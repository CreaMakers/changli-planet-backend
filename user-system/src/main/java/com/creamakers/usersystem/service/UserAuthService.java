package com.creamakers.usersystem.service;

import com.creamakers.usersystem.dto.request.LoginRequest;
import com.creamakers.usersystem.dto.request.PasswordUpdateRequest;
import com.creamakers.usersystem.dto.request.RegisterRequest;
import com.creamakers.usersystem.dto.request.UsernameCheckRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author yuxialuozi
 * @data 2024/10/29 - 17:19
 * @description
 */
public interface UserAuthService {

    ResponseEntity<GeneralResponse> register(RegisterRequest registerRequest);

    ResponseEntity<GeneralResponse> checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest);

    ResponseEntity<GeneralResponse> quit(String accessToken, String deviceId);

    ResponseEntity<GeneralResponse> refreshAuth(String auth);

    ResponseEntity<GeneralResponse> login(LoginRequest loginRequest, String deviceId,String accessToken);

    ResponseEntity<GeneralResponse> updatePassword(PasswordUpdateRequest request, String accessToken);
}
