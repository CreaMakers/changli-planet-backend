package com.creamakers.fresh.system.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 新鲜事请求类
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsRequest {

    @JsonProperty("user_id")
    private Long userId;  // 用户ID

    @JsonProperty("title")
    private String title;    // 新鲜事标题

    @JsonProperty("content")
    private String content;  // 新鲜事内容


    @JsonProperty("tags")
    private String tags;     // 新鲜事的标签 (可选)

    @JsonProperty("allow_comments")
    private Integer allowComments; // 是否允许评论，1为允许，0为不允许
}
