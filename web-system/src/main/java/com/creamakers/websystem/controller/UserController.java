package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


// 用户登录
@RestController("/web/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultVo<UserProfileResp> login(@RequestBody UserInfoReq userInfoReq) {
        return userService.login(userInfoReq);
    }

    @PostMapping("/logout")
    public ResultVo<Void> logout() {
        return userService.logout();
    }

    
}
