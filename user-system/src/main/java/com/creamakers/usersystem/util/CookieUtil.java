package com.creamakers.usersystem.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class CookieUtil {

    @Value("${JWT_ACCESS_TOKEN_EXPIRATION_TIME}")
    private Integer jwtAccessTokenExpirationTime;


    public Cookie createAccessTokenCookie(String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/"); // 设置 cookie 的路径
        cookie.setMaxAge(jwtAccessTokenExpirationTime / 1000); // 设置 cookie 过期时间，单位为秒
        cookie.setHttpOnly(true); // 设置 HttpOnly
        return cookie;
    }

    public String buildSetCookieHeader(Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        sb.append("Path=").append(cookie.getPath()).append("; ");
        if (cookie.getMaxAge() > 0) {
            sb.append("Max-Age=").append(cookie.getMaxAge()).append("; ");
        }
        if (cookie.isHttpOnly()) {
            sb.append("HttpOnly; ");
        }
        // 其他属性（如 SameSite）可以根据需求添加
        return sb.toString().trim();
    }

}
