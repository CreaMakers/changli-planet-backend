package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.UserInfoReq;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;

public interface UserService {
    ResultVo<UserProfileResp> login(UserInfoReq userInfoReq);

    ResultVo<Void> logout();
}
