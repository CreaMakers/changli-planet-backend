package com.creamakers.websystem.service.Impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.TokenContext;
import com.creamakers.websystem.context.UserIdContext;
import com.creamakers.websystem.context.UserNameContext;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.dao.UserProfileMapper;
import com.creamakers.websystem.dao.UserStatsMapper;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.dto.UserStats;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.*;
import com.creamakers.websystem.domain.vo.response.*;
import com.creamakers.websystem.enums.ErrorEnums;
import com.creamakers.websystem.service.UserService;
import com.creamakers.websystem.utils.JwtUtils;
import com.creamakers.websystem.utils.RedisUtil;
import io.netty.util.internal.StringUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private UserStatsMapper userStatsMapper;
    @Autowired
    private JwtUtils jwtUtils;
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
        /*
         * 把token放入黑名单中
         * */
        addBlackListToken();
        return ResultVo.success();
    }

    @Override
    public ResultVo<UserAllInfoResp> getCurrentUserProfile() {
        Long userId = UserIdContext.getCurrentId();
        if(userId == null) {
            return ResultVo.fail(CommonConst.ACCOUNT_NOT_FOUND);
        }
        UserProfileResp userProfileResp = BeanUtil.copyProperties(userProfileMapper.selectById(userId), UserProfileResp.class);
        UserResp userResp = BeanUtil.copyProperties(userMapper.selectById(userId), UserResp.class);
        UserStatsResp userStatsResp = BeanUtil.copyProperties(userStatsMapper.selectById(userId), UserStatsResp.class);


        return ResultVo.success(new UserAllInfoResp(userResp, userProfileResp, userStatsResp));
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
        /*
        * 把token放入黑名单中
        * */
        addBlackListToken();

        return ResultVo.success();
    }

    /*
    *
    * 根据id返回UserAllInfoResp
    * */
    @Override
    public ResultVo<UserAllInfoResp> findUserById(Long userId) {
        User user = userMapper.selectById(userId);
        UserProfile userProfile = userProfileMapper.selectById(userId);
        UserStats userStats = userStatsMapper.selectById(userId);

        if(user == null || userProfile == null || userStats == null) {
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
        }

        UserResp userResp = BeanUtil.copyProperties(user, UserResp.class);
        UserProfileResp userProfileResp = BeanUtil.copyProperties(userProfile, UserProfileResp.class);
        UserStatsResp userStatsResp = BeanUtil.copyProperties(userStats, UserStatsResp.class);


        return ResultVo.success(new UserAllInfoResp(userResp, userProfileResp, userStatsResp));
    }

    @Override
    public ResultVo<List<UserAllInfoResp>> findAllUsersInFos(String username, Integer isAdmin, Integer isDeleted, Integer isBanned, Integer page, Integer pageSize) {
        Page<User> pageParam = new Page<>(page, pageSize);
        Page<User> userPage = userMapper.selectPage(pageParam, Wrappers.<User>lambdaQuery()
                .eq(!StringUtil.isNullOrEmpty(username), User::getUsername, username)
                .eq(isAdmin != null, User::getIsAdmin, isAdmin)
                .eq(isDeleted != null, User::getIsDeleted, isDeleted)
                .eq(isBanned != null, User::getIsBanned, isBanned));
        List<User> users = userPage.getRecords();
        List<UserAllInfoResp> userAllInfoRespList = new ArrayList<>(users.size());
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            Long id = user.getUserId();
            UserProfile userProfile = userProfileMapper.selectById(id);
            UserStats userStats = userStatsMapper.selectById(id);
            UserResp userResp = BeanUtil.copyProperties(user, UserResp.class);
            UserProfileResp userProfileResp = BeanUtil.copyProperties(userProfile, UserProfileResp.class);
            UserStatsResp userStatsResp = BeanUtil.copyProperties(userStats, UserStatsResp.class);

            userAllInfoRespList.add(new UserAllInfoResp(userResp, userProfileResp, userStatsResp));
        }
        return ResultVo.success(userAllInfoRespList);
    }

    @Override
    public ResultVo<UserAllInfoResp> updateUserInfos(UserAllInfoReq userAllInfoReq) {

        UserReq userReq = userAllInfoReq.getUserReq();
        UserProfileReq userProfileReq = userAllInfoReq.getUserProfileReq();
        UserStatsReq userStatsReq = userAllInfoReq.getUserStatsReq();

        if (userReq != null && userReq.getUserId() != null) {
            // 先查询原用户信息
            User dbUser = userMapper.selectById(userReq.getUserId());
            if (dbUser == null) {
                return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
            }

            User user = BeanUtil.copyProperties(userReq, User.class);

            // 如果密码和数据库中的不一样，说明修改了密码，需要加密
            if (!dbUser.getPassword().equals(userReq.getPassword())) {
                String encryptedPassword = DigestUtils.md5DigestAsHex(userReq.getPassword().getBytes());
                if(user.getUserId() == UserIdContext.getCurrentId()) {
                    addBlackListToken();
                }
                user.setPassword(encryptedPassword);
            }

            int updated = userMapper.updateById(user);
            if (updated <= 0) {
                return ResultVo.fail("更新用户信息失败");
            }
        }

        if(userProfileReq != null && userProfileReq.getUserId() != null) {

            UserProfile dbUserProfile = userProfileMapper.selectById(userProfileReq.getUserId());
            if(dbUserProfile == null) {
                return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
            }
            UserProfile userProfile = BeanUtil.copyProperties(userProfileReq, UserProfile.class);
            int updated = userProfileMapper.updateById(userProfile);
            if(updated <= 0) {
                return ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, CommonConst.BAD_UPDATE_USER);
            }
        }

        if(userStatsReq != null && userStatsReq.getUserId() != null) {

            UserStats dbUserStats = userStatsMapper.selectById(userStatsReq.getUserId());
            if(dbUserStats == null) {
                return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
            }
            UserStats userStats = BeanUtil.copyProperties(userStatsReq, UserStats.class);

            int updated = userStatsMapper.updateById(userStats);
            if(updated <= 0) {
                return ResultVo.fail(CommonConst.RESULT_FAILURE_CODE, CommonConst.BAD_UPDATE_USER);
            }
        }
        return ResultVo.success();
    }

    private void addBlackListToken() {
        redisUtil.setValue(CommonConst.BLACKLIST_TOKEN_PREFIX + TokenContext.getCurrentToken(), "invalid", 2, TimeUnit.HOURS);
    }
}
