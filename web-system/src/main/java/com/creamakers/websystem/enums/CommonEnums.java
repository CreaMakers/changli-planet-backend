package com.creamakers.websystem.enums;

public enum CommonEnums {
    STATUS_ENABLE(0, "启用"),
    STATUS_DISABLE(1, "禁用"),

    STATUS_ACTIVATE(0, "未删除"),
    STATUS_DELETE(1, "已删除"),

    USER_TYPE_ADMIN(1, "运营组"),
    USER_TYPE_DEV(2, "开发组"),
    USER_TYPE_USER(0, "普通用户")


    ;

    private Integer code;
    private String msg;

    CommonEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
