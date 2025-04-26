package com.creamakers.usersystem.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailUpdateVerificationCodeRequest {
    private String email;
    private String currentPassword;
}
