package com.creamakers.usersystem.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration_time}")
    private long expirationTime;

    @Value("${jwt.refresh_token_expiration_time}")
    private long refreshTokenExpirationTime;

    // 生成 JWT
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    // 验证 JWT
    public boolean validateToken(String token, String username) {
        DecodedJWT decodedJWT = decodeToken(token);
        return (decodedJWT != null && decodedJWT.getSubject().equals(username) && !isTokenExpired(decodedJWT));
    }

    // 从 JWT 中提取用户名
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodeToken(token);
        return (decodedJWT != null) ? decodedJWT.getSubject() : null;
    }

    // 检查 JWT 是否过期
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    // 解码 JWT
    private DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (Exception e) {

             //logger.error("JWT decoding error: {}", e.getMessage());
            return null; // 或者根据需要抛出异常
        }
    }

    public String generateRefreshToken(String username) {
        // 生成一个较长有效期的 refresh token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime)) // 使用配置文件中的时间
                .sign(algorithm);
    }
}
