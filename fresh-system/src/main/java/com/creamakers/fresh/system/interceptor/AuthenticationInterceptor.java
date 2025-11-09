package com.creamakers.fresh.system.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.context.UserContext;
import com.creamakers.fresh.system.dao.UserMapper;
import com.creamakers.fresh.system.domain.dto.User;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.utils.JwtUtil;
import com.creamakers.fresh.system.utils.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${planet.redis.tokenKey}")
    private String REFRESH_TOKEN_PREFIX;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("authorization");

        // 检查 authorization 是否为空或格式不正确
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, CommonConst.UNAUTHORIZED, CommonConst.UNAUTHORIZED_ACCESS, null));
            return false;
        }

        String accessToken = authorization.substring(7);

        // 判断Token时间戳
        try {
            String username = jwtUtil.getUserNameFromToken(accessToken);
            String deviceId = jwtUtil.getDeviceIDFromToken(accessToken);
            Long timeStampFromToken = jwtUtil.getTimeStampFromToken(accessToken);
            String key = REFRESH_TOKEN_PREFIX + username + "-" + deviceId;
            String refresh_token = redisUtil.getValue(key);

            // 检查 refresh_token 是否存在
            if (refresh_token == null) {
                writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, CommonConst.UNAUTHORIZED, CommonConst.UNAUTHORIZED_ACCESS, null));
                return false;
            }

            Long newTimeStampFromToken = jwtUtil.getTimeStampFromToken(refresh_token);
            // 防止空指针异常并比较时间戳
            if (newTimeStampFromToken == null || timeStampFromToken == null || !newTimeStampFromToken.equals(timeStampFromToken)) {
                writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, CommonConst.UNAUTHORIZED, CommonConst.UNAUTHORIZED_ACCESS, null));
                return false;
            }
            //查找登录用户
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getUsername, username)
                    .eq(User::getIsDeleted, 0));
            if (user == null) {
                // 用户不存在
                writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, CommonConst.NOT_FOUND, CommonConst.USER_NOT_FOUND_MESSAGE, null));
                return false;
            }

            // 登录用户设置到上下文
            UserContext.set(user.getUserId().longValue(),username,accessToken);

            return true;
        } catch (JWTVerificationException e) {
            writeResponse(response, createResponseEntity(HttpStatus.UNAUTHORIZED, CommonConst.UNAUTHORIZED, CommonConst.UNAUTHORIZED_ACCESS, null));
            return false;
        }
    }

    private void writeResponse(HttpServletResponse response, ResponseEntity<ResultVo<Object>> responseEntity) throws Exception {
        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    public ResponseEntity<ResultVo<Object>> createResponseEntity(HttpStatus status, String code, String msg, Object data) {
        return ResponseEntity
                .status(status)
                .body(ResultVo.builder()
                        .code(code)
                        .msg(msg)
                        .data(data)
                        .build());
    }
}
