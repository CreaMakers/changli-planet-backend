package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResp {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 个性标签/个人描述
     */
    private String bio;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 性别: 0-男, 1-女, 2-其他
     */
    private Integer gender;

    /**
     * 年级
     */
    private String grade;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    private Integer isDeleted;

    /**
     * 用户展示信息描述
     */
    private String description;
}
