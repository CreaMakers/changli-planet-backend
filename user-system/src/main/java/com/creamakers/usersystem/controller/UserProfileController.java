package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/me/profile")
    public ResponseEntity<GeneralResponse> getProfile(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userProfileService.getProfile(accessToken);
    }


    @GetMapping("/{user_id}/profile")
    public ResponseEntity<GeneralResponse> getProfileByUserId(@PathVariable("user_id") String userId) {
        return userProfileService.getProfileByID(userId);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<GeneralResponse> updateProfile(@RequestBody UserProfileRequest request,
                                                         @RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userProfileService.updateInfo(request, accessToken);
    }


}
