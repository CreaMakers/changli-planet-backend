package com.creamakers.websystem.domain.dto;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户展示信息实体类
 *
 * @author YourName
 * @since 2023-XX-XX
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_profile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Integer userId;

    /**
     * 用户头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 个性标签/个人描述
     */
    @TableField("bio")
    private String bio;

    /**
     * 用户等级
     */
    @TableField("user_level")
    private Byte userLevel;

    /**
     * 性别: 0-男, 1-女, 2-其他
     */
    @TableField("gender")
    private Byte gender;

    /**
     * 年级
     */
    @TableField("grade")
    private String grade;

    /**
     * 出生日期
     */
    @TableField("birth_date")
    private LocalDate birthDate;

    /**
     * 所在地
     */
    @TableField("location")
    private String location;

    /**
     * 个人网站或社交链接
     */
    @TableField("website")
    private String website;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Byte isDeleted;

    /**
     * 用户展示信息描述
     */
    @TableField("description")
    private String description;
}