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
 * 新鲜事审核DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fresh_news_check")
public class FreshNewsCheck {
    // 主键ID
    @TableId(value = "fresh_news_check_id", type = IdType.AUTO)
    private Long freshNewsCheckId;

    // 关联的新鲜事ID
    @TableField(value = "fresh_news_id")
    private Long freshNewsId;

    // 标题
    @TableField(value = "title")
    private String title;

    // 内容
    @TableField(value = "content")
    private String content;

    // 审核图片URL
    @TableField(value = "image_url")
    private String imageUrl;

    // 审核状态（0：待审核，1：通过，2：拒绝）
    @TableField(value = "check_status")
    private Integer checkStatus;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 审核时间
    @TableField(value = "check_time")
    private LocalDateTime checkTime;
}
