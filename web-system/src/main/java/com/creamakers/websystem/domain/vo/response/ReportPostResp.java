package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class ReportPostResp {
        // 举报ID
        private Long reportId;
        // 被举报的帖子ID
        private Long postId;
        // 举报者的用户ID
        private Long reporterId;
        // 举报原因
        private String reason;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
        private LocalDateTime reportTime;
        // 处理状态: 0-未处理, 1-已处理
        private Integer status;
        // 处理描述
        private String processDescription;
        // 是否删除: false-未删除, true-已删除
        private Boolean isDeleted;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
        private LocalDateTime createTime;
        // 更新时间
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
        private LocalDateTime updateTime;
        // 帖子举报表描述
        private String description;
    }