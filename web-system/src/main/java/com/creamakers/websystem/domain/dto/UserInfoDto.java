package com.creamakers.websystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    // 用户名
    private String username;
    // 用户id
    private Long userId;
    // 用户目前access_token
    private String accessToken;
}
