package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("")
    public ResponseEntity<GeneralResponse> register(@RequestBody RegisterRequest registerRequest) {
        return userAuthService.register(registerRequest);
    }

    @PostMapping("/session")
    public ResponseEntity<GeneralResponse> login(
            @RequestBody LoginRequest loginRequest,
            @RequestHeader("deviceId") String deviceId,
            HttpServletResponse response) {
        return userAuthService.login(loginRequest, deviceId, response); // 将 deviceId 和 response 传递给服务层
    }


    @DeleteMapping("/session")
    public GeneralResponse quit(@RequestHeader(value = "Authorization") String accessToken) {
        return userAuthService.quit(accessToken);
    }


    @PutMapping("/me/token")
    public GeneralResponse refreshAuth(@RequestBody AccessTokenRequest accessTokenRequest) {
        return userAuthService.refreshAuth(accessTokenRequest.getAccessToken());
    }


    @GetMapping("/availability")
    public GeneralResponse usernameCheck(@ModelAttribute UsernameCheckRequest usernameCheckRequest) {
        return userAuthService.checkUsernameAvailability(usernameCheckRequest);
    }

    @PutMapping("/me/password")
    public GeneralResponse updatePassword(@RequestBody PasswordUpdateRequest request, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader("token");
        return userAuthService.updatePassword(request, accessToken);
    }

}
