package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("report_post")
public class ReportPost {
    // 举报ID
    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    // 被举报的帖子ID
    @TableField(value = "post_id")
    private Long postId;

    // 举报者的用户ID
    @TableField(value = "reporter_id")
    private Long reporterId;

    // 举报原因
    @TableField(value = "reason")
    private String reason;

    // 举报时间
    @TableField(value = "report_time")
    private LocalDateTime reportTime;

    // 处理状态: 0-未处理, 1-已处理
    @TableField(value = "status")
    private Integer status;

    // 处理描述
    @TableField(value = "process_description")
    private String processDescription;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 帖子举报表描述
    @TableField(value = "description")
    private String description;
}
