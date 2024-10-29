package com.creamakers.websystem.domain.dto;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户动态数据实体类
 *
 * @author dcelysia
 * @since 2024-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_stats")
public class UserStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Integer userId;

    /**
     * 学号，唯一
     */
    @TableField("student_number")
    private String studentNumber;

    /**
     * 发表文章数
     */
    @TableField("article_count")
    private Integer articleCount;

    /**
     * 评论次数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 发言次数
     */
    @TableField("statement_count")
    private Integer statementCount;

    /**
     * 收到点赞次数
     */
    @TableField("liked_count")
    private Integer likedCount;

    /**
     * 硬币数量
     */
    @TableField("coin_count")
    private Integer coinCount;

    /**
     * 经验值
     */
    @TableField("xp")
    private Integer xp;

    /**
     * 考核通过状态
     */
    @TableField("quiz_type")
    private Integer quizType;

    /**
     * 最近登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

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
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 用户动态数据描述
     */
    @TableField("description")
    private String description;
}