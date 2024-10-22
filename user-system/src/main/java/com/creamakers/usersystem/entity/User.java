package com.creamakers.usersystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;  // 使用 Jakarta 的 NotNull 注解
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author yuxialuozi
 * @data 2024/10/22 - 20:23
 * @description 用户实体类
 */
@Data // Lombok注解，自动生成getter、setter、toString、hashCode、equals等方法
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
@Builder // 生成构造者模式
@Accessors(chain = true) // 支持链式调用
@ApiModel(description = "用户实体类") // Swagger注解，用于生成API文档
public class User {

    @ApiModelProperty(value = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 255, message = "用户名长度应在3到255之间")
    @ApiModelProperty(value = "用户名", example = "john_doe")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 128, message = "密码长度应在6到128个字符之间")
    @ApiModelProperty(value = "加密存储的密码", example = "加密的密码")
    private String password;

    @NotNull(message = "管理员权限不能为空")
    @ApiModelProperty(value = "管理员权限: 0-普通用户, 1-运营组, 2-开发组", example = "0")
    private Byte isAdmin;

    // 使用 Jackson 的 @JsonFormat 注解，确保返回给客户端时格式为 yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2024-10-22 10:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "最后更新时间", example = "2024-10-22 12:00:00")
    private Date updateTime;

    @NotNull(message = "是否删除字段不能为空")
    @ApiModelProperty(value = "是否删除: 0-未删除，1-已删除", example = "0")
    private Byte isDeleted;

    @NotNull(message = "是否封禁字段不能为空")
    @ApiModelProperty(value = "是否封禁: 0-未封禁，1-已封禁", example = "0")
    private Byte isBanned;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    @ApiModelProperty(value = "用户描述", example = "这是一个普通用户")
    private String description;
}
