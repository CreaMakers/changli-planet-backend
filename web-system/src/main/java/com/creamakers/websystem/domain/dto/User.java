package com.creamakers.websystem.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    /*
    * 管理员权限: 0-普通用户, 1-运营组，2-开发组
    * */
    private Integer isAdmin;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDeleted;
    private Boolean isBanned;
    private String description;
}
