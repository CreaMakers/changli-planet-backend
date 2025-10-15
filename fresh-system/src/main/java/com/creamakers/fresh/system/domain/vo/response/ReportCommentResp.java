package com.creamakers.fresh.system.domain.vo.response;

import com.creamakers.fresh.system.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 举报评论结果响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCommentResp {

    // 举报ID
    private Long reportId;

    // 被举报的评论ID
    private Long commentId;

    // 举报者的用户ID
    private Long reporterId;

    // 举报原因
    private String reason;

    // 举报类型
    private ReportType reportType;

    // 举报状态: 0-未处理, 1-已处理
    private Integer status;

    // 处理描述
    private String processDescription;

    // 是否删除: 0-未删除, 1-已删除
    private Integer isDeleted;

    // 举报时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime reportTime;

    // 处理时间
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime processTime;
}

