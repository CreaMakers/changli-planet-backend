package com.creamakers.usersystem.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 自定义用户服务异常类，使用 Lombok 简化代码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceException extends RuntimeException {
    private String code;

    public UserServiceException(String message, String code) {
        super(message);
        this.code = code;
    }
}
