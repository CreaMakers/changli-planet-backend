package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_group_announcement")
public class ChatGroupAnnouncement {

    // 公告ID
    @TableId(value = "announcement_id", type = IdType.AUTO)
    private Long announcementId;

    // 所属群聊ID
    @TableField(value = "group_id")
    private Long groupId;

    // 发布用户ID
    @TableField(value = "user_id")
    private Long userId;

    // 公告标题
    @TableField(value = "title")
    private String title;

    // 公告内容
    @TableField(value = "content")
    private String content;

    // 是否置顶公告: 1-置顶, 0-不置顶
    @TableField(value = "is_pinned")
    private Integer isPinned;

    // 公告创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 公告更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    // 公告描述
    @TableField(value = "description")
    private String description;
}
