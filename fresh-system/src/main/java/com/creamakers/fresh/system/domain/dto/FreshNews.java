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
 * 新鲜事DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news")
public class FreshNews {

    @TableId(value = "fresh_news_id", type = IdType.AUTO)
    private Long freshNewsId;

    // 用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 标题
    @TableField(value = "title")
    private String title;

    // 内容
    @TableField(value = "content")
    private String content;

    // 发布地址
    @TableField(value = "address")
    private String address;

    // 图片路径
    @TableField(value = "images")
    private String images;

    // 标签
    @TableField(value = "tags")
    private String tags;

    // 点赞数量
    @TableField(value = "liked")
    private Long liked;

    // 评论数量
    @TableField(value = "comments")
    private Integer comments;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 是否允许评论
    @TableField(value = "allow_comments")
    private Integer allowComments;

    // 被收藏数
    @TableField(value = "favorites_count")
    private Integer favoritesCount;
}
