package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private ApkService apkService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody RegisterRequest registerRequest) {
        return userAuthService.register(registerRequest);
    }

    @PostMapping("/sessions/password")
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


    @PostMapping("/sessions/email")
     public  ResponseEntity<GeneralResponse> loginByEmail(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) LoginByEmailRequest loginByEmailRequest,
            @RequestHeader(value = "deviceId", required = false) String deviceId
     ){
        String accessToken = null;
        if (authorization != null && !authorization.isEmpty()) {
            accessToken = authorization.substring(7);
        }
        return userAuthService.loginByEmail(loginByEmailRequest, deviceId, accessToken);
    }



    @PostMapping("/auth/verification-code/register")
    public ResponseEntity<GeneralResponse> registerVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest){
        return userAuthService.registerVerificationCode(verificationCodeRequest);
    }

    @PostMapping("/auth/verification-code/login")
    public ResponseEntity<GeneralResponse> loginVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest) {
        return userAuthService.loginVerificationCode(verificationCodeRequest);
    }

    @PostMapping("/auth/verification-code/email-change")
    public ResponseEntity<GeneralResponse> emailUpdateVerificationCode(@RequestHeader(value = "Authorization") String authorization,
                                                                       @RequestBody EmailUpdateVerificationCodeRequest emailUpdateVerificationCodeRequest) {
        String accessToken = null;
        if (authorization != null && !authorization.isEmpty()) {
            accessToken = authorization.substring(7);
        }
        return userAuthService.emailUpdateVerificationCode(emailUpdateVerificationCodeRequest,accessToken);
    }

    @DeleteMapping("/session")
    public ResponseEntity<GeneralResponse> quit(@RequestHeader(value = "Authorization") String authorization,
                                                @RequestHeader("deviceId") String deviceId) {
        String accessToken = authorization.substring(7);
        return userAuthService.quit(accessToken,deviceId);
    }

    @PutMapping("/me/email")
    public ResponseEntity<GeneralResponse> updateEmail(@RequestBody EmailUpdateRequest emailUpdateRequest, @RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.updateEmail(emailUpdateRequest, accessToken);
    }


    @PutMapping("/me/token")
    public ResponseEntity<GeneralResponse> refreshAuth(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.refreshAuth(accessToken);
    }


    @GetMapping("/availability/{username}")
    public ResponseEntity<GeneralResponse> usernameCheck(@PathVariable String username) {
        UsernameCheckRequest usernameCheckRequest = new UsernameCheckRequest(username);
        return userAuthService.checkUsernameAvailability(usernameCheckRequest);
    }

    @PutMapping("/me/password")
    public ResponseEntity<GeneralResponse> updatePassword(@RequestBody PasswordUpdateRequest request, @RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.updatePassword(request, accessToken);
    }

    @PutMapping("/me/username")
    public ResponseEntity<GeneralResponse> updateUsername(@RequestBody UsernameUpdateRequest request, @RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userAuthService.updateUsername(request, accessToken);
    }


    @GetMapping("/apk")
    public ResponseEntity<GeneralResponse> checkApkVersion(@RequestParam("versionCode") Integer versionCode,
                                                           @RequestParam("versionName") String versionName) {

        return apkService.checkApkVersion(versionCode,versionName);
    }

}
