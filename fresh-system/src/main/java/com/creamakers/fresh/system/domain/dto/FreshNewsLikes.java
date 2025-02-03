package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 新鲜事点赞DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news_likes")
public class FreshNewsLikes {

    @TableId(value = "like_id", type = IdType.AUTO)
    private Long likeId;

    // 点赞用户ID
    @TableField(value = "user_id")
    private Integer userId;

    // 新鲜事ID
    @TableField(value = "news_id")
    private Long newsId;

    // 是否删除，0：未删除，1：已删除
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    // 点赞时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}