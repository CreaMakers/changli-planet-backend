package com.creamakers.websystem.interceptor;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
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
                .orElseThrow(() -> new RuntimeException("token异常，请重新登陆"));

        String userId = Optional.ofNullable(jwtUtils.getUidOrNull(token))
                .orElseThrow(() -> new RuntimeException("用户不存在，请重新登陆"));


        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                        .eq(User::getUserId, userId)))
                .orElseThrow(() -> new RuntimeException("用户查询失败，token无效"));

        if(user.getIsAdmin() == CommonEnums.USER_TYPE_USER.getCode()) {
            throw new RuntimeException("账号权限不足");
        }
        if(user.getIsBanned() || user.getIsDeleted()) {
            throw new RuntimeException("账号异常，已被删除或封禁");
        }

        return true;
    }
}
