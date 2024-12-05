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

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody RegisterRequest registerRequest) {
        return userAuthService.register(registerRequest);
    }

    @PostMapping("/session")
    public ResponseEntity<GeneralResponse> login(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) LoginRequest loginRequest,
            @RequestHeader(value = "deviceId", required = false) String deviceId) {

        String accessToken = null;
        if (authorization != null && !authorization.isEmpty()) {
            accessToken = authorization.substring(7);
        }

        return userAuthService.login(loginRequest, deviceId, accessToken);
    }


    @DeleteMapping("/session")
    public ResponseEntity<GeneralResponse> quit(@RequestHeader(value = "Authorization") String authorization,
                                                @RequestHeader("deviceId") String deviceId) {
        String accessToken = authorization.substring(7);
        return userAuthService.quit(accessToken,deviceId);
    }


    @PutMapping("/me/token")
    public ResponseEntity<GeneralResponse> refreshAuth(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.refreshAuth(accessToken);
    }


    @GetMapping("/availability")
    public ResponseEntity<GeneralResponse> usernameCheck(@RequestBody UsernameCheckRequest usernameCheckRequest) {
        return userAuthService.checkUsernameAvailability(usernameCheckRequest);
    }


    @PutMapping("/me/password")
    public ResponseEntity<GeneralResponse> updatePassword(@RequestBody PasswordUpdateRequest request, @RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.updatePassword(request, accessToken);
    }

}
