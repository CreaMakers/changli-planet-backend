package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


// 用户登录
@RestController("/web/users")
public class UserController {

    @PostMapping("/login")
    public ResultVo<UserProfileResp> login(@RequestBody UserInfoReq userInfoReq) {
        return ResultVo.success(new UserProfileResp());
    }
}
