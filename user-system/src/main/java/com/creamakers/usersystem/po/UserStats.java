package com.creamakers.usersystem.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户动态数据表
 * @author Hayaizo
 * @date 2024-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_stats")
public class UserStats implements Serializable {

    @TableId(type = IdType.AUTO)
    /**
    * 用户id
    */
    private Integer userId;

    /**
     * 用户动态数据描述
     */
    private String description;

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
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastLoginTime;

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