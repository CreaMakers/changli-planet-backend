package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PasswordChangeReq;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.LoginTokenResp;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.service.UserService;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


// 用户登录
@RestController
@RequestMapping("/web/users")
public class UserController {

    @Autowired
    private UserService userService;

    /*
    * 用户登录
    * */
    @PostMapping("/login")
    public ResultVo<LoginTokenResp> login(@RequestBody UserInfoReq userInfoReq) {
        return userService.login(userInfoReq);
    }

    /*
    * 用户退出
    * */
    @PostMapping("/logout")
    public ResultVo<Void> logout() {
        return userService.logout();
    }

    /*
    * 获取当前用户的信息
    * */
    @GetMapping("/me")
    public ResultVo<UserProfile> getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }

    /*
    * 修改当前用户的密码
    * */

    @PutMapping("/me")
    public ResultVo<Void> modifyCurrentUserPassword(@RequestBody PasswordChangeReq passwordChangeReq) {
        return userService.modifyCurrentUserPassword(passwordChangeReq);
    }

}
