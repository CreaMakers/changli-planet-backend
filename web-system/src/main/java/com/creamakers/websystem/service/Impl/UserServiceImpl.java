package com.creamakers.websystem.service.Impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.dao.UserProfileMapper;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.service.UserService;
import com.creamakers.websystem.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResultVo<UserProfileResp> login(UserInfoReq userInfoReq) {
        String password = new String(DigestUtils.md5DigestAsHex(userInfoReq.getPassword().getBytes()));

        User user = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, userInfoReq.getName())
        )).orElse(null);

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

        UserProfile userProfile = userProfileMapper.selectOne(Wrappers.<UserProfile>lambdaQuery()
                .eq(UserProfile::getUserId, user.getUserId()));

        String token = jwtUtils.createToken(user.getUserId());

        UserProfileResp userProfileResp = new UserProfileResp();
        BeanUtils.copyProperties(userProfile, userProfileResp);
        userProfileResp.setToken(token);

        return ResultVo.success(userProfileResp);
    }

    @Override
    public ResultVo<Void> logout() {
        return null;
    }
}
