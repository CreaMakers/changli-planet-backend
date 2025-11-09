package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新鲜事子评论VO类
 * */
@Data
public class FreshNewsChildCommentResp {
    // 子评论ID
    @JsonProperty(value = "commentId")
    private Long Id;

    // 父评论ID
    @JsonProperty(value = "parentCommentId")
    private Long fatherCommentId;

    // 关联的新鲜事ID
    @JsonProperty(value = "freshNewsId")
    private Long freshNewsId;

    // 点赞数量
    @JsonProperty(value = "likedCount")
    private Integer likedCount;

    // 评论内容
    @JsonProperty(value = "content")
    private String content;

    // 用户ID
    @JsonProperty(value = "userId")
    private Long userId;

    // 用户名
    @JsonProperty(value = "userName")
    private String userName;

    // 用户头像URL
    @JsonProperty(value = "userAvatar")
    private String userAvatar;

    // 评论发布的地址
    @JsonProperty(value = "commentIp")
    private String commentIp;

    // 评论发布的时间
    @JsonProperty(value = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private LocalDateTime commentTime;

    // 是否有效: 0-未有效, 1-已有效
    @JsonProperty(value = "isActive")
    private Integer isActive;
}
