package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 新鲜事评论DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news_comments")
public class FreshNewsComment {

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    // 用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 新鲜事ID
    @TableField(value = "news_id")
    private Long newsId;

    // 父评论ID
    @TableField(value = "parent_id")
    private Long parentId;

    // 根评论ID
    @TableField(value = "root")
    private Long root;

    // 评论内容
    @TableField(value = "content")
    private String content;

    // 点赞数量
    @TableField(value = "liked")
    private Integer liked;

    // 是否删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
