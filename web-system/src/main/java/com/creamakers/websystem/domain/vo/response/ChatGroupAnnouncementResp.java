package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupAnnouncementResp {

    // 公告ID
    private Long announcementId;

    // 群聊ID
    private Long groupId;

    // 发布用户ID
    private Long userId;

    // 公告标题
    private String title;

    // 公告内容
    private String content;

    // 是否置顶公告
    private Boolean isPinned;

    // 公告创建时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createTime;

    // 公告更新时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updateTime;

    // 是否删除
    private Boolean isDeleted;

    // 公告描述
    private String description;
}

