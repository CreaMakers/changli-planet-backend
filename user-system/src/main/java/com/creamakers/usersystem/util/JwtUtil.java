package com.creamakers.usersystem.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_ACCESS_TOKEN_EXPIRATION_TIME}")
    private Long jwtAccessTokenExpirationTime;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION_TIME}")
    private Long jwtRefreshTokenExpirationTime;

    /**
     * 生成普通JWT令牌
     */
    public String generateAccessToken(String username, String deviceID, Long timeStamp) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.create()
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationTime))
                    .withClaim("username", username)
                    .withClaim("deviceID", deviceID)
                    .withClaim("timeStamp", timeStamp)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username, String deviceID, Long timeStamp) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.create()
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshTokenExpirationTime))
                    .withClaim("username", username)
                    .withClaim("deviceID", deviceID)
                    .withClaim("timeStamp", timeStamp)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证JWT令牌的有效性
     */
    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 检查是否过期
            if (isTokenExpired(decodedJWT)) {
                System.out.println("JWT已过期");
                return false;
            }

            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            System.out.println("JWT验证失败");
            return false;
        }
    }

    /**
     * 解码JWT令牌但不进行验证
     */
    public DecodedJWT decodeTokenWithoutVerification(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从JWT令牌中获取设备ID
     */
    public String getDeviceIDFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("deviceID").asString();
        }
        return null;
    }

    /**
     * 从JWT令牌中获取用户名
     */
    public String getUserNameFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("username").asString();
        }
        throw new JWTVerificationException("无效凭证");
    }

    /**
     * 从JWT令牌中获取时间戳
     */
    public Long getTimeStampFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("timeStamp").asLong();
        }
        return null;
    }

    /**
     * 检查JWT令牌是否过期
     */
    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    /**
     * 解码JWT令牌并进行验证
     */
    private DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
