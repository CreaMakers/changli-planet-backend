package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 方法描述:
     *
     * 注册用户
     *
     * @param registerRequest
     * @return com.creamakers.usersystem.dto.response.GeneralResponse
     * @author Hayaizo
     **/
    @PostMapping("/register")
    public GeneralResponse register(@RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    /**
     * 方法描述:
     *
     * 用户登录
     *
     * @param loginRequest
     * @return com.creamakers.usersystem.dto.response.GeneralResponse
     * @author Hayaizo
     **/
    @PostMapping("/session")
    public GeneralResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    /**
     * 方法描述:
     *
     * 用户退出
     * @return com.creamakers.usersystem.dto.response.GeneralResponse
     * @author Hayaizo
     **/
    
    @DeleteMapping("/session")
    public GeneralResponse quit(@RequestHeader(value = "Authorization") String accessToken) {
        return userService.quit(accessToken);
    }

    /**
     * 方法描述:
     * 刷新token
     * @return com.creamakers.usersystem.dto.response.GeneralResponse
     * @author Hayaizo
     **/
    
    @PutMapping("/me/token")
    public GeneralResponse refreshAuth(@RequestBody AccessTokenRequest accessTokenRequest) {
        return userService.refreshAuth(accessTokenRequest.getAccessToken());
    }

    /**
     * 方法描述:
     *
     * 查询某个用户是否存在
     *
     * @param usernameCheckRequest
     * @return com.creamakers.usersystem.dto.response.GeneralResponse
     * @author yuxialuozi
     **/
    @GetMapping("/availability")
    public GeneralResponse usernameCheck(@ModelAttribute UsernameCheckRequest usernameCheckRequest) {
        return userService.checkUsernameAvailability(usernameCheckRequest);
    }

    @GetMapping("/me/profile")
    public GeneralResponse getProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return userProfileService.getProfile(accessToken);
    }

}
