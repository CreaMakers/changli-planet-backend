package com.creamakers.websystem.domain.dto;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户基础信息实体类
 *
 * @author dcelysia
 * @since 2024-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码，经过加密存储
     */
    @TableField("password")
    private String password;

    /**
     * 管理员权限: 0-普通用户, 1-运营组，2-开发组
     */
    @TableField("is_admin")
    private Integer isAdmin;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 是否封禁: 0-未封禁，1-已封禁
     */
    @TableField("is_banned")
    private Integer isBanned;

    /**
     * 用户描述
     */
    @TableField("description")
    private String description;
}