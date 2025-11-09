package com.creamakers.fresh.system.exception;

import com.creamakers.fresh.system.constants.CommonConst;
import lombok.Getter;

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
