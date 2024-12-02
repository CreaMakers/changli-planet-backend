package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResp {

    // 帖子ID
    private Long postId;

    // 所属群聊ID
    private Long groupId;

    // 发布用户ID
    private Long userId;

    // 帖子标题
    private String title;

    // 帖子内容
    private String content;

    // 帖子类别 (0-general, 1-tutorial, 2-article, 3-experience)
    private Integer category;

    // 是否加精 (0-否, 1-是)
    private Boolean isPinned;

    // 浏览人数
    private Integer viewCount;

    // 被投币量
    private Integer coinCount;

    // 帖子创建时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createTime;

    // 帖子更新时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updateTime;

    // 是否删除 (0-未删除, 1-已删除)
    private Boolean isDeleted;

    // 帖子描述
    private String description;
}
