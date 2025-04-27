package com.creamakers.usersystem.service;

import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
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

    ResponseEntity<GeneralResponse> updateUsername(UsernameUpdateRequest request, String accessToken);

    ResponseEntity<GeneralResponse> registerVerificationCode(VerificationCodeRequest verificationCodeRequest);

    ResponseEntity<GeneralResponse> loginVerificationCode(VerificationCodeRequest verificationCodeRequest);

    ResponseEntity<GeneralResponse> loginByEmail(LoginByEmailRequest loginByEmailRequest, String deviceId, String accessToken);

    ResponseEntity<GeneralResponse> emailUpdateVerificationCode(EmailUpdateVerificationCodeRequest verificationCodeRequest, String accessToken);

    ResponseEntity<GeneralResponse> updateEmail(EmailUpdateRequest emailUpdateRequest, String accessToken);

    ResponseEntity<GeneralResponse> forgetPasswordVerificationCode(VerificationCodeRequest verificationCodeRequest);

    ResponseEntity<GeneralResponse> resetPassword(PasswordResetRequest passwordResetRequest);
}
