package com.creamakers.usersystem.common.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.creamakers.usersystem.consts.ErrorMessage;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        String authorization = request.getHeader("authorization");
        return true;
//        // 检查 authorization 是否为空或格式不正确
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null));
//            return false;
//        }
//
//        String accessToken = authorization.substring(7);
//
//        // 判断Token时间戳
//        try {
//            String username = jwtUtil.getUserNameFromToken(accessToken);
//            String deviceId = jwtUtil.getDeviceIDFromToken(accessToken);
//            Long timeStampFromToken = jwtUtil.getTimeStampFromToken(accessToken);
//            String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceId;
//            String refresh_token = stringRedisTemplate.opsForValue().get(key);
//
//            // 检查 refresh_token 是否存在
//            if (refresh_token == null) {
//                writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null));
//                return false;
//            }
//
//            Long newTimeStampFromToken = jwtUtil.getTimeStampFromToken(refresh_token);
//            // 防止空指针异常并比较时间戳
//            if (newTimeStampFromToken == null || timeStampFromToken == null || !newTimeStampFromToken.equals(timeStampFromToken)) {
//                writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null));
//                return false;
//            }
//            return true;
//        } catch (JWTVerificationException e) {
//            writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, HttpCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_ACCESS, null));
//            return false;
//        }
    }

    private void writeResponse(HttpServletResponse response, ResponseEntity<GeneralResponse> responseEntity) throws Exception {
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    public ResponseEntity<GeneralResponse> createResponseEntity(HttpStatus status, String code, String msg, Object data) {
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .code(code)
                        .msg(msg)
                        .data(data)
                        .build());
    }
}
