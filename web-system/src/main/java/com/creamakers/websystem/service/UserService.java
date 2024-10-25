package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PasswordChangeReq;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.LoginTokenResp;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;

public interface UserService {
    ResultVo<LoginTokenResp> login(UserInfoReq userInfoReq);

    ResultVo<Void> logout();

    ResultVo<UserProfile> getCurrentUserProfile();

    ResultVo<Void> modifyCurrentUserPassword(PasswordChangeReq passwordChangeReq);
}
