package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService extends IService<UserProfile> {
    ResponseEntity<GeneralResponse> getProfile(String accessToken);

    ResponseEntity<GeneralResponse> getProfileByID(String userID);

    ResponseEntity<GeneralResponse> updateInfo(UserProfileRequest request,String accessToken);

    Boolean initializeUserProfile(User user);

    ResponseEntity<GeneralResponse> saveAvatar(MultipartFile avatar, String accessToken);

    boolean updatEmailByUser(User user);
}
