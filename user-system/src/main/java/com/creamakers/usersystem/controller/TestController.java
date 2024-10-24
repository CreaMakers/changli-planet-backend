package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.exception.UserServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user-exception")
    public void throwUserServiceException() {
        // 模拟抛出 UserServiceException
        throw new UserServiceException("Test UserServiceException", "USER_ERROR");
    }
}
