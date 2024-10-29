package com.creamakers.websystem.domain.vo.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户基础信息响应类
 *
 * @author dcelysia
 * @since 2024-10-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReq {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，经过加密存储
     */
    private String password;

    /**
     * 管理员权限: 0-普通用户, 1-运营组，2-开发组
     */
    private Integer isAdmin;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    private Integer isDeleted;

    /**
     * 是否封禁: 0-未封禁，1-已封禁
     */
    private Integer isBanned;


    /**
     * 用户描述
     */
    private String description;
}