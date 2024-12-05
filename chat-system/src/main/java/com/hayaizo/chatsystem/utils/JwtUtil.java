package com.hayaizo.chatsystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_ACCESS_TOKEN_EXPIRATION_TIME}")
    private Long jwtAccessTokenExpirationTime;

    @Value("${JWT_REFRESH_TOKEN_EXPIRATION_TIME}")
    private Long jwtRefreshTokenExpirationTime;

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证Token是否有效以及存在redis
     * @param token
     * @return
     */
    public boolean verify(String token) {
        // Token校验
        boolean validateToken = validateToken(token);
        if(!validateToken){
            return false;
        }

        String username = getUserNameFromToken(token);
        String deviceID = getDeviceIDFromToken(token);
        Long timeStampFromToken = getTimeStampFromToken(token);
        String key = TOKEN_PREFIX+username+"-"+deviceID;
        String refresh_token = stringRedisTemplate.opsForValue().get(key);
        Long timeStampFromToken1 = getTimeStampFromToken(refresh_token);
        if(timeStampFromToken.equals(timeStampFromToken1)){
            return true;
        }
        if(Objects.nonNull(refresh_token)){
            return true;
        }
        return false;
    }

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
