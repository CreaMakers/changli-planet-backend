package com.creamakers.fresh.system.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsCommentResp {

    // 评论ID
    private Long commentId;

    // 用户ID
    private Long userId;

    // 父评论ID，如果是一级评论则为0
    private Long parentId;

    // 评论内容
    private String content;

    // 点赞数量
    private Integer liked;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime updateTime;
}
