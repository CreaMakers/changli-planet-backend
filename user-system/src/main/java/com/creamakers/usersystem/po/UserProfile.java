package com.creamakers.usersystem.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户展示信息表
 * @author Hayaizo
 * @date 2024-10-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_profile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 用户id
    */
    private Integer userId;

    /**
    * 用户头像url
    */
    private String avatarUrl;

    /**
     * 用户姓名注册登录用
     */
    @TableField("username")
    private String username;

    private String emailbox;

    /**
     * 用户账号名，用户自定义
     */
    @TableField("account")
    private String account;

    /**
    * 个性标签/个人描述
    */
    private String bio;

    /**
     * 用户展示信息描述
     */
    private String description;

    /**
    * 用户等级
    */
    private Integer userLevel;

    /**
    * 性别: 0-男， 1-女， 2-其他
    */
    private Integer gender;

    /**
    * 年级
    */
    private String grade;

    /**
    * 出生日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
    * 所在地
    */
    private String location;

    /**
    * 个人网站或社交链接
    */
    private String website;

    /**
    * 记录创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
    * 记录更新时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
    * 是否删除: 0-未删除，1-已删除
    */
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}