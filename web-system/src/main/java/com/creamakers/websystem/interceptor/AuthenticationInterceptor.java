package com.creamakers.websystem.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.UserIdContext;
import com.creamakers.websystem.context.UserNameContext;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.enums.CommonEnums;
import com.creamakers.websystem.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 获取token
        String token = Optional.ofNullable(request.getHeader("token"))
                .orElseThrow(() -> new RuntimeException(CommonConst.TOKEN_NOT_FOUND));

        String userName = Optional.ofNullable(jwtUtils.getUserNameOrNull(token))
                .orElseThrow(() -> new RuntimeException(CommonConst.ACCOUNT_NOT_FOUND));

        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                        .eq(User::getUsername, userName)))
                .orElseThrow(() -> new RuntimeException(CommonConst.TOKEN_INVALID));

        /*
        * 保存两个上下文
        * */
        UserIdContext.set(user.getUserId());
        UserNameContext.set(user.getUsername());

        if(user.getIsAdmin() == CommonEnums.USER_TYPE_USER.getCode()) {
            throw new RuntimeException(CommonConst.INSUFFICIENT_PERMISSION);
        }
        if(user.isBanned() || user.isDeleted()) {
            throw new RuntimeException(CommonConst.ACCOUNT_DISABLED_OR_BANNED);
        }
        return true;
    }
}
