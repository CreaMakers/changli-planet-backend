package com.creamakers.websystem.service.Impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.UserIdContext;
import com.creamakers.websystem.context.UserNameContext;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.dao.UserProfileMapper;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PasswordChangeReq;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.LoginTokenResp;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.enums.ErrorEnums;
import com.creamakers.websystem.service.UserService;
import com.creamakers.websystem.utils.JwtUtils;
import com.creamakers.websystem.utils.RedisUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResultVo<LoginTokenResp> login(UserInfoReq userInfoReq) {
        String password = new String(DigestUtils.md5DigestAsHex(userInfoReq.getPassword().getBytes()));
        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, userInfoReq.getUsername())
        )).orElse(null);
//        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
//                .eq(User::getUsername, userInfoReq.getUsername()));


        /*
        * 用户不存在
        * */
        if(user == null) {
            return ResultVo.fail(CommonConst.ACCOUNT_NOT_FOUND);
        }
        /*
        * 密码错误
        * */
        if( !password.equals(user.getPassword()) ) {
            return ResultVo.fail(CommonConst.PASSWORD_ERROR);
        }
        /*
        * 权限不足
        * */
        if(user.getIsAdmin() == 0) {
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }

        String accessToken = jwtUtils.createAccessToken(user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUsername());

        // 缓存刷新 token
        cacheRefreshToken(user.getUsername(), refreshToken);

        LoginTokenResp loginTokenResp = new LoginTokenResp(accessToken, CommonConst.TOKEN_EXPIRATION_TIME);
        return ResultVo.success(loginTokenResp);
    }

    /*
    * 刷新accessToken和refreshToken的缓存
    * */
    private void cacheRefreshToken(String username, String refreshToken) {
        redisUtil.setValue(CommonConst.REFRESH_TOKEN_PREFIX + username, refreshToken, 1, TimeUnit.DAYS);
    }

    public ResultVo<LoginTokenResp> refreshToken(String token) {
        String username = jwtUtils.extractUidFromExpiredToken(token);
        if(StringUtil.isNullOrEmpty(username)) {
            return ResultVo.fail(CommonConst.TOKEN_INVALID);
        }
        String cachedRefreshToken = redisUtil.getValue(CommonConst.REFRESH_TOKEN_PREFIX + username);

        // 判断是不是最新的accessToken
        if(cachedRefreshToken != null && jwtUtils.compareAccessTokenWithRefreshToken(token, cachedRefreshToken)) {
            // 获取新的token和refreshToken
            String newAccessToken = jwtUtils.createAccessToken(username);
            String newRefreshToken = jwtUtils.createRefreshToken(username);
            // 自动刷新refresh_token过期时间，和刷新accessToken
            cacheRefreshToken(username, newRefreshToken);

            LoginTokenResp loginTokenResp = new LoginTokenResp(newAccessToken, CommonConst.TOKEN_EXPIRATION_TIME);
            return ResultVo.success(loginTokenResp);
        } else {
            return ResultVo.fail(ErrorEnums.UNAUTHORIZED.getCode(), ErrorEnums.UNAUTHORIZED.getMsg());
        }
    }
    /*
    * 通过用户名去删除redis中的refreshToken
    * */
    private void removeRefreshTokenByUserName(String name) {
        redisUtil.deleteValue(CommonConst.REFRESH_TOKEN_PREFIX + name);
    }

    @Override
    public ResultVo<Void> logout() {
        String username = UserNameContext.getCurrentName();
        removeRefreshTokenByUserName(username);
        return ResultVo.success();
    }

    @Override
    public ResultVo<UserProfile> getCurrentUserProfile() {
        Long userId = UserIdContext.getCurrentId();
        if(userId == null) {
            return ResultVo.fail(CommonConst.ACCOUNT_NOT_FOUND);
        }
        UserProfile userProfile = userProfileMapper.selectById(userId);
        UserProfileResp userProfileResp = new UserProfileResp();
        BeanUtil.copyProperties(userProfile, userProfileResp);
        return ResultVo.success(userProfile);
    }

    @Override
    public ResultVo<Void> modifyCurrentUserPassword(PasswordChangeReq passwordChangeReq) {
        Long userId = UserIdContext.getCurrentId();
        User user = userMapper.selectById(userId);
        // 老密码错误
        if(user.getPassword().equals(passwordChangeReq.getOldPassword())) {
            return ResultVo.fail(ErrorEnums.UNAUTHORIZED.getCode(), ErrorEnums.UNAUTHORIZED.getMsg());
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex(passwordChangeReq.getNewPassword().getBytes());
        user.setPassword(encryptedPassword);
        userMapper.updateById(user);
        return ResultVo.success();
    }
}
