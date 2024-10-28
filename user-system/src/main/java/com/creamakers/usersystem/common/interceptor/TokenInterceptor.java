package com.creamakers.usersystem.common.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.creamakers.usersystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Value("${REFRESH_TOKEN_PREFIX}")
    private String REFRESH_TOKEN_PREFIX;

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public TokenInterceptor(JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("token");

        // 判断Token时间戳
        try{
            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceId = jwtUtil.getDeviceIDFromToken(accessToken);
            Long timeStampFromToken = jwtUtil.getTimeStampFromToken(accessToken);
            String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceId;
            String refresh_token = stringRedisTemplate.opsForValue().get(key);

            // 检查 refresh_token 是否存在
            if (refresh_token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            Long newTimeStampFromToken = jwtUtil.getTimeStampFromToken(refresh_token);
            // 防止空指针异常并比较时间戳
            if (newTimeStampFromToken == null || timeStampFromToken == null || !newTimeStampFromToken.equals(timeStampFromToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            return true;
        }catch (JWTVerificationException e){
            throw new JWTVerificationException(e.getMessage());
        }
    }
}
