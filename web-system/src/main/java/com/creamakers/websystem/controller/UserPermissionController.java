package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserPermissionResp;

import com.creamakers.websystem.domain.vo.response.UserResp;
import com.creamakers.websystem.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web")
public class UserPermissionController {
    @Autowired
    private UserPermissionService userPermissionService;
    /*
    查看所有用户权限
     */
    @GetMapping("/permissions")
    public ResultVo<List<UserPermissionResp>> getAllUserPermission(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize){
        return userPermissionService.getAllUserPermission(page, pageSize);
    }
    @GetMapping("/users/{userId}/permissions")
    public ResultVo<UserPermissionResp> getUserPermissionById(@PathVariable("userId") Long userId){
        return userPermissionService.getUserPermissionById(userId);
    }
    /*
    文档中请求体和前面的UserResp是一样的
     */
    @PutMapping("/users/{userId}/permissions")
    public ResultVo<UserPermissionResp> updateUserPermissions(@PathVariable("userId") Long userId, @RequestBody UserResp userResp){
        return userPermissionService.updateUserPermissions(userId,userResp);
    }
}
