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
 * 通知DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notification")
public class Notification {

    // 通知ID
    @TableId(value = "notification_id", type = IdType.AUTO)
    private Long notificationId;

    // 发送者ID
    @TableField(value = "sender_id")
    private Long senderId;

    // 接收者ID
    @TableField(value = "receiver_id")
    private Long receiverId;

    // 通知内容
    @TableField(value = "content")
    private String content;

    // 是否已读
    @TableField(value = "is_read")
    private Integer isRead;

    // 是否已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 发送时间
    @TableField(value = "send_time")
    private LocalDateTime sendTime;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 通知描述
    @TableField(value = "description")
    private String description;
}
