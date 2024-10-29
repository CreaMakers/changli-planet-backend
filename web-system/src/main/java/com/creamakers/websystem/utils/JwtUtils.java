package com.creamakers.websystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Description: jwt的token生成与解析
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-03
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    @Value("${planet.jwt.secret}")
    private String secret;
    /*
    * 默认2小时过期
    * */
    @Value("${planet.jwt.expiration_time}")
    private Long EXPIRATION_TIME;

    @Value("${planet.jwt.refresh_expiration_time}")
    private Long REFRESH_EXPIRATION_TIME;
    private static final String USER_NAME_CLAIM = "username";
    private static final String CREATE_TIME = "createTime";

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     */
    public String createAccessToken(String username) {
        Date expirationDate = new Date(new Date().getTime() + EXPIRATION_TIME);
        // build token
        String token = JWT.create()
                .withClaim(USER_NAME_CLAIM, username) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret)); // signature
        return token;
    }

    public String createRefreshToken(String username) {
        Date expirationDate = new Date(new Date().getTime() + REFRESH_EXPIRATION_TIME);
        // build token
        String token = JWT.create()
                .withClaim(USER_NAME_CLAIM, username) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret)); // signature
        return token;
    }
    /*
    *  创建两个具有相同时间戳的accessToken和refreshToken
    * */
    public String[] createAccessTokenAndRefreshToken(String username) {
        Date expirationDate = new Date(new Date().getTime() + REFRESH_EXPIRATION_TIME);
        Date date = new Date();
        // build token
        String accessToken = JWT.create()
                .withClaim(USER_NAME_CLAIM, username) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, date)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret)); // signature
        String refreshToken = JWT.create()
                .withClaim(USER_NAME_CLAIM, username)
                .withClaim(CREATE_TIME, date)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
        return new String[]{accessToken, refreshToken};
    }

    public boolean compareAccessTokenWithRefreshToken(String accessToken, String refreshToken) {
        DecodedJWT decodedAccessToken = JWT.decode(accessToken);
        DecodedJWT decodedRefreshToken = JWT.decode(refreshToken);

        String access_username = decodedAccessToken.getClaim(USER_NAME_CLAIM).asString();
        String refresh_username = decodedRefreshToken.getClaim(USER_NAME_CLAIM).asString();

        if(!access_username.equals(refresh_username)) {
            return false;
        }
        Date accessDate = decodedAccessToken.getClaim(CREATE_TIME).asDate();
        Date refreshDate = decodedRefreshToken.getClaim(CREATE_TIME).asDate();
        if(refreshDate.compareTo(accessDate) != 0) {
            return false;
        }
        return true;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     */
    public Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
        }
        return null;
    }


    /**
     * 根据Token获取uid
     *
     * @param token
     * @return uid
     */
    public String getUserNameOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(USER_NAME_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }



    /**
     * 从JWT中提取用户ID，即使令牌已过期
     *
     * @param token JWT令牌
     * @return 用户ID，如果无法提取则返回null
     */
    public String extractUidFromExpiredToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            // 只解码JWT，不验证签名和过期时间
            DecodedJWT jwt = JWT.decode(token);
            Claim uidClaim = jwt.getClaim(USER_NAME_CLAIM);
            return uidClaim != null ? uidClaim.asString() : null;
        } catch (Exception e) {
            log.error("从过期令牌中提取UID时出错,token:{}", token, e);
            return null;
        }
    }

}
