package com.creamakers.fresh.system.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsDetailResp {

    // 新鲜事ID
    private Long freshNewsId;

    // 用户ID
    private Long userId;

    // 新鲜事标题
    private String title;

    // 新鲜事内容
    private String content;

    // 新鲜事图片列表
    private List<String> images;

    // 新鲜事标签列表
    private List<String> tags;

    // 点赞数量
    private Integer liked;

    // 评论数量
    private Integer comments;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 是否删除
    private Integer isDeleted;

    // 是否允许评论
    private Integer allowComments;

    // 被收藏数
    private Integer favoritesCount;
}
