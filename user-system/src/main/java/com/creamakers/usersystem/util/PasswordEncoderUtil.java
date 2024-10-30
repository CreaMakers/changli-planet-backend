package com.creamakers.usersystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 加密原始密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // 调用一次加密方法
    }

    /**
     * 验证输入的密码是否与加密后的密码匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
