package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PasswordChangeReq;
import com.creamakers.websystem.domain.vo.request.UserAllInfoReq;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.LoginTokenResp;
import com.creamakers.websystem.domain.vo.response.UserAllInfoResp;
import com.creamakers.websystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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
    public ResultVo<UserAllInfoResp> getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }

    /*
    * 修改当前用户的密码
    * */

    @PutMapping("/me")
    public ResultVo<Void> modifyCurrentUserPassword(@RequestBody PasswordChangeReq passwordChangeReq) {
        return userService.modifyCurrentUserPassword(passwordChangeReq);
    }

    /*
    * 根据搜索条件去查询符合的所有用户的全部信息
    * */
    @GetMapping
    public ResultVo<List<UserAllInfoResp>> getAllUsersInfos(
            @RequestParam(value = "userName", required = false) String username,
            @RequestParam(value = "isAdmin", required = false) Integer isAdmin,
            @RequestParam(value = "isDeleted", required = false) Integer isDeleted,
            @RequestParam(value = "isBanned", required = false) Integer isBanned,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
            ) {
        return userService.findAllUsersInFos(username, isAdmin, isDeleted, isBanned, page, pageSize);
    }

    /*
    * 通过用户ID去查询某个用户的全部信息
    * */
    @GetMapping("/{userId}")
    public ResultVo<UserAllInfoResp> getUserById(@PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }

    @PutMapping
    public ResultVo<UserAllInfoResp> updateUserInfos(@RequestBody UserAllInfoReq userAllInfoReq) {
        return userService.updateUserInfos(userAllInfoReq);
    }
}
