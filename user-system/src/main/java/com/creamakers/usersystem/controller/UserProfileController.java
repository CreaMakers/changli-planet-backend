package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/me/profile")
    public GeneralResponse getProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return userProfileService.getProfile(accessToken);
    }


    @GetMapping("/{user_id}/profile")
    public GeneralResponse getProfileByUserId(@PathVariable("user_id") String userId) {
        return userProfileService.getProfileByID(userId);
    }

    @PutMapping("/me/profile")
    public GeneralResponse updateProfile(@RequestBody UserProfileRequest request, HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader("token");
        return userProfileService.updateInfo(request, accessToken);
    }


}
