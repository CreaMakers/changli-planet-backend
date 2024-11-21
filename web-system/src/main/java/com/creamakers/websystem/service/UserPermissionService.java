package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserPermissionResp;
import com.creamakers.websystem.domain.vo.response.UserResp;

import java.util.List;

public interface UserPermissionService {

    ResultVo<List<UserPermissionResp>> getAllUserPermission(Integer page, Integer pageSize);

    ResultVo<UserPermissionResp> getUserPermissionById(Long userId);

    ResultVo<UserPermissionResp> updateUserPermissions(Long userId, UserResp userResp);
}
