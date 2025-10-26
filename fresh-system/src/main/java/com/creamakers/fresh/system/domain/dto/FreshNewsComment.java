package com.creamakers.fresh.system.domain.dto;

import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 新鲜事评论DTO类
 */

@Accessors(chain = true)
public interface FreshNewsComment {


    // 评论ID
    Long getId();

    FreshNewsComment setId(Long id);

    // 关联的新鲜事ID
    Long getFreshNewsId();

    FreshNewsComment setFreshNewsId(Long freshNewsId);

    // 点赞数量
    Integer getLikedCount();

    FreshNewsComment setLikedCount(Integer likedCount);

    // 评论内容
    String getContent();

    FreshNewsComment setContent(String content);

    // 用户ID
    Long getUserId();

    FreshNewsComment setUserId(Long userId);

    // 用户名
    String getUserName();

    FreshNewsComment setUserName(String userName);

    // 用户头像URL
    String getUserAvatar();

    FreshNewsComment setUserAvatar(String userAvatar);

    // 评论发布的地址
    String getCommentIp();

    FreshNewsComment setCommentIp(String commentIp);

    // 评论发布的时间
    LocalDateTime getCommentTime();

    FreshNewsComment setCommentTime(LocalDateTime commentTime);

    // 是否有效: 0-未有效, 1-已有效
    Integer getIsActive();

    FreshNewsComment setIsActive(Integer isActive);

    // 是否删除: 0-未删除, 1-已删除
    Integer getIsDeleted();

    FreshNewsComment setIsDeleted(Integer isDeleted);

    // 创建时间
    LocalDateTime getCreateTime();

    FreshNewsComment setCreateTime(LocalDateTime createTime);

    // 更新时间
    LocalDateTime getUpdateTime();

    FreshNewsComment setUpdateTime(LocalDateTime updateTime);
}
