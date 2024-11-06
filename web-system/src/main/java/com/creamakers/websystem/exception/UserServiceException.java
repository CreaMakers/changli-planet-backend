package com.creamakers.websystem.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceException extends RuntimeException {
    String code;

    public UserServiceException(String message, String code) {
        super(message);
        this.code = code;
    }
}
