package com.creamakers.websystem.domain.dto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group_post")
public class Post {

    // 帖子ID
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    // 所属群聊ID
    @TableField(value = "group_id")
    private Long groupId;

    // 发布用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 帖子标题
    @TableField(value = "title")
    private String title;

    // 帖子内容
    @TableField(value = "content")
    private String content;

    // 帖子类别 (0-general, 1-tutorial, 2-article, 3-experience)
    @TableField(value = "category")
    private Integer category;

    // 是否加精 (0-否, 1-是)
    @TableField(value = "is_pinned")
    private Integer isPinned;

    // 浏览人数
    @TableField(value = "view_count")
    private Integer viewCount;

    // 被投币量
    @TableField(value = "coin_count")
    private Integer coinCount;

    // 帖子创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 帖子更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除 (0-未删除, 1-已删除)
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 帖子描述
    @TableField(value = "description")
    private String description;
}
