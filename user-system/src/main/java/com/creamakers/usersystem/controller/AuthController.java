package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.dto.request.*;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.service.UserStatsService;
import com.creamakers.usersystem.service.ViolationRecordService;
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

    @Autowired
    private UserStatsService userStatsService;

    @Autowired
    private ViolationRecordService violationRecordService;

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

    /**
     * 获取用户展示信息
     * @param request
     * @return
     */
    @GetMapping("/me/profile")
    public GeneralResponse getProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return userProfileService.getProfile(accessToken);
    }

    /**
     * 获取单个用户的展示信息
     * @param userId
     * @return
     */
    @GetMapping("/{user_id}/profile")
    public GeneralResponse getProfileByUserId(@PathVariable("user_id") String userId) {
        return userProfileService.getProfileByID(userId);
    }

    /**
     * 获取用户动态的信息
     * @param request
     * @return
     */
    @GetMapping("/me/stats")
    public GeneralResponse getStats(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return userStatsService.getStats(accessToken);
    }

    @GetMapping("/{user_id}/stats")
    public GeneralResponse getStatsByUserId(@PathVariable("user_id") String userId) {
        return userStatsService.getStatsById(userId);
    }

    @PutMapping("/me/profile")
    public GeneralResponse updateProfile(@RequestBody UserProfileRequest request,HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader("token");
        return userProfileService.updateInfo(request,accessToken);
    }

    @PutMapping("/me/password")
    public GeneralResponse updatePassword(@RequestBody PasswordUpdateRequest request,HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader("token");
        return userService.updatePassword(request,accessToken);
    }

    @GetMapping("/me/violations")
    public GeneralResponse getViolations(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return violationRecordService.getViolations(accessToken);
    }

    @GetMapping("/{user_id}/violations")
    public GeneralResponse getViolationsByUserId(@PathVariable("user_id") String userId) {
        return violationRecordService.getViolationsByUserId(Integer.parseInt(userId));
    }

    @PostMapping("/me/student-number")
    public GeneralResponse bindStudentNumber(@RequestBody BindStudentNumberRequest bindStudentNumberRequest,HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        String studentNumber = bindStudentNumberRequest.getStudentNumber();
        return userStatsService.setStudentNumber(studentNumber,accessToken);
    }

}
