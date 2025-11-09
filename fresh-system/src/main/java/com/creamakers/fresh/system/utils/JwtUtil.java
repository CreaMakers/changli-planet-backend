package com.creamakers.fresh.system.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${planet.jwt.secret}")
    private String jwtSecret;

    @Value("${planet.jwt.expiration_time}")
    private Long jwtAccessTokenExpirationTime;

    @Value("${planet.jwt.refresh_expiration_time}")
    private Long jwtRefreshTokenExpirationTime;

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


    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 检查是否过期
            if (isTokenExpired(decodedJWT)) {
                log.info("Token is expired");
                return false;
            }

            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            log.info("Token is not valid");
            return false;
        }
    }


    public DecodedJWT decodeTokenWithoutVerification(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getDeviceIDFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("deviceID").asString();
        }
        return null;
    }


    public String getUserNameFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("username").asString();
        }
        throw new JWTVerificationException("无效凭证");
    }

    public Long getTimeStampFromToken(String token) {
        DecodedJWT jwt = decodeTokenWithoutVerification(token);
        if (jwt != null) {
            return jwt.getClaim("timeStamp").asLong();
        }
        return null;
    }


    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }


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
