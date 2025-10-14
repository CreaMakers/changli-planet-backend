package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResp {

    // 通知ID
    private Long notificationId;

    // 发送者ID
    private Long senderId;

    // 接收者ID
    private Long receiverId;

    // 通知类型
    private Integer notificationType;

    // 通知内容
    private String content;

    // 是否已读 (0: 未读, 1: 已读)
    private Integer isRead;

    // 是否已删除 (0: 未删除, 1: 已删除)
    private Integer isDeleted;

    // 发送时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sendTime;

    // 描述
    private String description;
}
