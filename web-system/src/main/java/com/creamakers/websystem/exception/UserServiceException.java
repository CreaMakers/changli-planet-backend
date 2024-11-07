package com.creamakers.websystem.exception;

import com.creamakers.websystem.constants.CommonConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserServiceException extends RuntimeException {
    String code;

    public UserServiceException(String message, String code) {
        super(message);
        this.code = code;
    }
    public UserServiceException(String message) {
        super(message);
        this.code = CommonConst.BAD_REQUEST_CODE;
    }
}
