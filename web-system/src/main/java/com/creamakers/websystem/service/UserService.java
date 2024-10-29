package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PasswordChangeReq;
import com.creamakers.websystem.domain.vo.request.UserAllInfoReq;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.LoginTokenResp;
import com.creamakers.websystem.domain.vo.response.UserAllInfoResp;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;

import java.util.List;

public interface UserService {
    ResultVo<LoginTokenResp> login(UserInfoReq userInfoReq);

    ResultVo<Void> logout();

    ResultVo<UserAllInfoResp> getCurrentUserProfile();

    ResultVo<Void> modifyCurrentUserPassword(PasswordChangeReq passwordChangeReq);

    ResultVo<UserAllInfoResp> findUserById(Long userId);

    ResultVo<List<UserAllInfoResp>> findAllUsersInFos(String username, Integer isAdmin, Integer isDeleted, Integer isBanned, Integer page, Integer pageSize);

    ResultVo<UserAllInfoResp> updateUserInfos(UserAllInfoReq userAllInfoReq);
}
