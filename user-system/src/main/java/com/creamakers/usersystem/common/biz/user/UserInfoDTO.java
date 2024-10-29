package com.creamakers.usersystem.common.biz.user;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserInfoDTO {

    @ApiModelProperty(value = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 255, message = "用户名长度应在3到255之间")
    @ApiModelProperty(value = "用户名", example = "john_doe")
    private String username;


    @NotNull(message = "管理员权限不能为空")
    @ApiModelProperty(value = "管理员权限: 0-普通用户, 1-运营组, 2-开发组", example = "0")
    private Byte isAdmin;

    @ApiModelProperty("用户token")
    private String token;
}
