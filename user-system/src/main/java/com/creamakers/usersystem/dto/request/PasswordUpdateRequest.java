package com.creamakers.usersystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码更新请求体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

    private String newPassword;

    private String confirmPassword;
}