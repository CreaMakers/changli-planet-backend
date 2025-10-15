package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.context.UserContext;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserPermissionResp;
import com.creamakers.websystem.domain.vo.response.UserResp;
import com.creamakers.websystem.enums.ErrorEnums;
import com.creamakers.websystem.exception.UserServiceException;
import com.creamakers.websystem.service.UserPermissionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
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
    @Transactional(rollbackFor = Exception.class)//更新失败事物回滚
    public ResultVo<UserPermissionResp> updateUserPermissions(Long userId, UserResp userResp) {
        /*
        先检查一下权限
         */
        User nowUser = userMapper.selectById(UserContext.getUserId());
        if(nowUser.getIsAdmin() == 0){
            return ResultVo.fail(ErrorEnums.FORBIDDEN.getCode(), ErrorEnums.FORBIDDEN.getMsg());
        }

        try {
            int update = userMapper.update(
                    BeanUtil.copyProperties(userResp, User.class),
                    new LambdaUpdateWrapper<User>().eq(User::getUserId,userResp.getUserId())
            );
            if(update<=0){
                //更新失败，抛出异常，事物回滚
                throw new UserServiceException(CommonConst.BAD_UPDATE_USER);
            }
            log.info("success update user permissions to: {},by userid: {}",userResp.getIsAdmin(),userResp.getUserId());
        } catch (Exception e) {
            log.warn("Failed to update user permissions for userid: {}",userResp.getUserId());
            throw e;
        }
        return ResultVo.success();
    }

    private UserPermissionResp convertToUserPermissionResp(User user) {
        UserPermissionResp resp = new UserPermissionResp();
        BeanUtil.copyProperties(user, resp);
        return resp;
    }

}
