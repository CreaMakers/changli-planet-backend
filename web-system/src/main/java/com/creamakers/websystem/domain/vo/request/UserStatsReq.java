package com.creamakers.websystem.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户动态数据响应类
 *
 * @author dcelysia
 * @since 2024-10-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsReq {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学号，唯一
     */
    private String studentNumber;

    /**
     * 发表文章数
     */
    private Integer articleCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 发言次数
     */
    private Integer statementCount;

    /**
     * 收到点赞次数
     */
    private Integer likedCount;

    /**
     * 硬币数量
     */
    private Integer coinCount;

    /**
     * 经验值
     */
    private Integer xp;

    /**
     * 考核通过状态
     */
    private Integer quizType;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;


    /**
     * 是否删除: 0-未删除，1-已删除
     */
    private Integer isDeleted;

    /**
     * 用户动态数据描述
     */
    private String description;
}