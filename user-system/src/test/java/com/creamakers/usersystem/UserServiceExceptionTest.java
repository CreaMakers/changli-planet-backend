package com.creamakers.usersystem;

import com.creamakers.usersystem.controller.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;


import com.creamakers.usersystem.exception.UserServiceException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceExceptionTest {

    @Test
    public void testUserServiceExceptionWithErrorCode() {
        // 构造带有错误代码的异常
        String errorCode = "USER_NOT_FOUND";
        String message = "用户不存在";
        UserServiceException exception = new UserServiceException(message, errorCode);


        throw  exception;

    }

    @Test
    public void testUserServiceExceptionWithoutErrorCode() {
        // 构造不带错误代码的异常
        String message = "用户数据出错";
        UserServiceException exception = new UserServiceException(message, null);

       throw  exception;
    }
}
