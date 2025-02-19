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
 * 收藏的新鲜事DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news_favorites")
public class FreshNewsFavorites {

    @TableId(value = "favorites_id", type = IdType.AUTO)
    private Long favoritesId;

    // 用户ID，关联用户表，表示收藏该新鲜事的用户
    @TableField(value = "user_id")
    private Integer userId;

    // 新鲜事ID，关联新鲜事表，表示被收藏的新鲜事
    @TableField(value = "news_id")
    private Long newsId;

    // 收藏时间，记录用户收藏该新鲜事的时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 是否删除，0：未删除，1：已删除，逻辑删除字段
    @TableField(value = "is_deleted")
    private Boolean isDeleted;
}
