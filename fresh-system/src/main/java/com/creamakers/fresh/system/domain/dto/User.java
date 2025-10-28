package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_profile") // 用户展示信息表(便于查询用户名和用户头像)
public class User {

    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户头像url
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 用户姓名注册登录用
     */
    @TableField("username")
    private String username;

    /**
     * 用户邮箱
     */
    @TableField(value = "emailbox")
    private String emailbox;

    /**
     * 用户账号名，用户自定义
     */
    @TableField("account")
    private String account;

    /**
     * 个性标签/个人描述
     */
    @TableField(value = "bio")
    private String bio;

    /**
     * 用户展示信息描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 用户等级
     */
    @TableField(value = "user_level")
    private Integer userLevel;

    /**
     * 性别: 0-男， 1-女， 2-其他
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 年级
     */
    @TableField(value = "grade")
    private String grade;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    @TableField(value = "birth_date")
    private LocalDate birthDate;

    /**
     * 所在地
     */
    @TableField(value = "location")
    private String location;

    /**
     * 个人网站或社交链接
     */
    @TableField(value = "website")
    private String website;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;
}