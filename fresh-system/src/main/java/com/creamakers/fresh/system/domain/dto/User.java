package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    // 用户名
    @TableField(value = "username")
    private String username;

    // 密码
    @TableField(value = "password")
    private String password;

    // 管理员权限
    @TableField(value = "is_admin")
    private Integer isAdmin;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 最后更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 是否封禁: 0-未封禁, 1-已封禁
    @TableField(value = "is_banned")
    private Integer isBanned;
}