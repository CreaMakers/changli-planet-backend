package com.creamakers.websystem.domain.vo.response;

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
     * 用户展示信息描述
     */
    private String description;
}
