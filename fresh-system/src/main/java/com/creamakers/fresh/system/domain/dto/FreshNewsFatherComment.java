package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新鲜事评论父评论DTO类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news_father_comments")
public class FreshNewsFatherComment implements FreshNewsComment , Serializable {
    //序列化ID
    @Serial
    private static final long serialVersionUID = -6497495412904537347L;

    // 父评论ID
    @TableId(value = "id", type = IdType.AUTO)
    private Long Id;

    // 关联的新鲜事ID
    @TableField(value = "fresh_news_id")
    private Long freshNewsId;

    // 点赞数量
    @TableField(value = "liked_count")
    private Integer likedCount;

    // 评论内容
    @TableField(value = "content")
    private String content;

    // 用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 用户名
    @TableField(value = "user_name")
    private String userName;

    // 用户头像URL
    @TableField(value = "user_avatar")
    private String userAvatar;

    // 评论发布的地址
    @TableField(value = "comment_ip")
    private String commentIp;

    // 评论发布的时间
    @TableField(value = "comment_time")
    private LocalDateTime commentTime;

    // 是否有效: 0-未有效, 1-已有效
    @TableField(value = "is_active")
    private Integer isActive;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
