package com.creamakers.fresh.system.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新鲜事评论请求类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsCommentRequest {

    @JsonProperty("user_id")
    private Long userId;  // 用户ID

    @JsonProperty("content")
    private String content;  // 评论内容

    @JsonProperty("parent_id")
    private Long parentId = 0L; // 父评论ID，默认为0表示一级评论

    @JsonProperty("news_id")
    private Long newsId;  // 新鲜事ID
}
