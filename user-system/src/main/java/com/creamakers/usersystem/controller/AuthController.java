package com.creamakers.usersystem.controller;

import com.creamakers.usersystem.dto.GeneralResponse;
import com.creamakers.usersystem.dto.LoginRequest;
import com.creamakers.usersystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/users/")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/session")
    public GeneralResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
