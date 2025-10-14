package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResp {

    // 评论ID
    private Long commentId;

    // 所属帖子ID
    private Long postId;

    // 评论用户ID
    private Long userId;

    // 父评论ID，表示是否回复别人的评论
    private Long parentCommentId;

    // 评论内容
    private String content;

    // 评论时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 评论更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 是否删除: false-未删除, true-已删除
    private Boolean isDeleted;

    // 评论描述
    private String description;
}

