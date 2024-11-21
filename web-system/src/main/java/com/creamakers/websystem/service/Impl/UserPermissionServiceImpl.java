package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.UserContext;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserPermissionResp;
import com.creamakers.websystem.domain.vo.response.UserResp;
import com.creamakers.websystem.enums.ErrorEnums;
import com.creamakers.websystem.service.UserPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {
    /*
    *查看所有用户权限
     */
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultVo<List<UserPermissionResp>> getAllUserPermission(Integer page, Integer pageSize) {
        /*
        先检查一下权限
         */
        User nowUser = userMapper.selectById(UserContext.getUserId());
        if(nowUser.getIsAdmin() == 0){
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }
        Page<User> pageParam = new Page<>(page, pageSize);
        Page<User> userPage = userMapper.selectPage(pageParam, new QueryWrapper<User>().eq("is_deleted", 0));
        List<User> users = userPage.getRecords();
        List<UserPermissionResp> userPermissionRespList = users.stream()
                .map(this::convertToUserPermissionResp)
                .collect(Collectors.toList());
        return ResultVo.success(userPermissionRespList);
    }

    @Override
    public ResultVo<UserPermissionResp> getUserPermissionById(Long userId) {
        User user = userMapper.selectById(userId);
        if(user == null) {
            return ResultVo.fail(CommonConst.ACCOUNT_NOT_FOUND);
        }
        /*
         * 权限不足
         * */
        if(user.getIsAdmin() == 0) {
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }
        UserPermissionResp userPermissionResp = convertToUserPermissionResp(user);
        return ResultVo.success(userPermissionResp);
    }

    @Override
    public ResultVo<UserPermissionResp> updateUserPermissions(Long userId, UserResp userResp) {
        /*
        先检查一下权限
         */
        User nowUser = userMapper.selectById(UserContext.getUserId());
        if(nowUser.getIsAdmin() == 0){
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }
        return null;
    }

    private UserPermissionResp convertToUserPermissionResp(User user) {
        UserPermissionResp resp = new UserPermissionResp();
        BeanUtil.copyProperties(user, resp);
        return resp;
    }

}
