package com.creamakers.fresh.system.domain.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportFreshNewsRequest {

    @NotNull(message = "被举报的新鲜事ID不能为空")
    private Long freshNewsId;

    @NotNull(message = "举报者ID不能为空")
    private Long reporterId;

    @NotNull(message = "举报原因不能为空")
    private String reason;

    @NotNull(message = "举报类型不能为空")
    private String reportType;

    private Integer status = 0; // 默认未处理

    private Integer isDeleted = 0; // 默认未删除

    private String description;
}
