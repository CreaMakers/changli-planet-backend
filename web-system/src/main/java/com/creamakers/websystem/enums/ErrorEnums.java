package com.creamakers.websystem.enums;

public enum ErrorEnums {
    UNAUTHORIZED("401", "身份认证失败"),
    FORBIDDEN("403", "权限不足")
    ;


    private String code;
    private String msg;

    ErrorEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
