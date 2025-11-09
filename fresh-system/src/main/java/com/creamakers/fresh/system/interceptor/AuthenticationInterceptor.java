package com.creamakers.fresh.system.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.context.UserContext;
import com.creamakers.fresh.system.dao.UserMapper;
import com.creamakers.fresh.system.domain.dto.User;
import com.creamakers.fresh.system.exception.UserServiceException;
import com.creamakers.fresh.system.utils.JwtUtils;
import com.creamakers.fresh.system.utils.RedisUtil;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 获取token
        String token = Optional.ofNullable(request.getHeader("token"))
                .orElseThrow(() -> new UserServiceException(CommonConst.TOKEN_NOT_FOUND));

        String userName = Optional.ofNullable(jwtUtils.getUserNameOrNull(token))
                .orElseThrow(() -> new UserServiceException(CommonConst.ACCOUNT_NOT_FOUND));

        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                        .eq(User::getUsername, userName)))
                .orElseThrow(() -> new UserServiceException(CommonConst.TOKEN_INVALID));

        /*
        * 保存三个上下文
        * */
        UserContext.set(user.getUserId().longValue(), user.getUsername(), token);
        /*
        * 判断有无加入黑名单
        * */
        String redisToken = redisUtil.getValue(CommonConst.BLACKLIST_TOKEN_PREFIX + token);
        log.info("redis黑名单里存的token：{}", redisToken);
        if(!StringUtil.isNullOrEmpty(redisToken)) {
            throw new UserServiceException(CommonConst.TOKEN_INVALID);
        }

        if(user.getIsDeleted() == 1 ) {
            throw new UserServiceException(CommonConst.ACCOUNT_DISABLED_OR_BANNED);
        }
        return true;
    }
}
