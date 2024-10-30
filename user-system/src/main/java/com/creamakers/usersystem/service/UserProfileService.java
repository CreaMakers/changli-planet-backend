package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.UserProfile;

public interface UserProfileService extends IService<UserProfile> {
    GeneralResponse getProfile(String accessToken);

    GeneralResponse getProfileByID(String userID);

    GeneralResponse updateInfo(UserProfileRequest request,String accessToken);

    Boolean initializeUserProfile(Integer userId);
}
