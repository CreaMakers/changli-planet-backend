package com.creamakers.fresh.system.domain.vo.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 新鲜事父评论VO类
 * */
@Data
public class FreshNewsFatherCommentResp {
    // 父评论ID
    @JsonProperty(value = "comment_id")
    private Long Id;

    // 关联的新鲜事ID
    @JsonProperty(value = "fresh_news_id")
    private Long freshNewsId;

    // 点赞数量
    @JsonProperty(value = "liked")
    private Integer likedCount;

    // 评论内容
    @JsonProperty(value = "content")
    private String content;

    // 用户ID
    @JsonProperty(value = "user_id")
    private Long userId;

    // 用户名
    @JsonProperty(value = "user_name")
    private String userName;

    // 用户头像URL
    @JsonProperty(value = "user_avatar")
    private String userAvatar;

    // 评论发布的地址
    @JsonProperty(value = "comment_ip")
    private String commentIp;

    // 评论发布的时间
    @JsonProperty(value = "create_time")
    private LocalDateTime commentTime;

    // 是否有效: 0-未有效, 1-已有效
    @JsonProperty(value = "is_active")
    private Integer isActive;

    // 子评论列表
    @JsonProperty(value = "reply_list")
    private List<FreshNewsChildCommentResp> childCommentsList;
}
