package com.creamakers.websystem.interceptor;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.BaseContext;
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

        Long userId = Optional.ofNullable(jwtUtils.getUidOrNull(token))
                .orElseThrow(() -> new RuntimeException(CommonConst.ACCOUNT_NOT_FOUND));

        BaseContext.set(userId);

        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                        .eq(User::getUserId, userId)))
                .orElseThrow(() -> new RuntimeException(CommonConst.TOKEN_INVALID));

        if(user.getIsAdmin() == CommonEnums.USER_TYPE_USER.getCode()) {
            throw new RuntimeException(CommonConst.INSUFFICIENT_PERMISSION);
        }
        if(user.isBanned() || user.isDeleted()) {
            throw new RuntimeException(CommonConst.ACCOUNT_DISABLED_OR_BANNED);
        }
        return true;
    }
}
