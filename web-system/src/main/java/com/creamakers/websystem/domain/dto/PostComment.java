package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post_comment")
public class PostComment {

    // 评论ID
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    // 所属帖子ID
    @TableField(value = "post_id")
    private Long postId;

    // 评论用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 父评论ID，表示是否回复别人的评论
    @TableField(value = "parent_comment_id")
    private Long parentCommentId;

    // 评论内容
    @TableField(value = "content")
    private String content;

    // 评论时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 评论更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    // 评论描述
    @TableField(value = "description")
    private String description;
}

