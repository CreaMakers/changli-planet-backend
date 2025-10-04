package com.creamakers.fresh.system.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreshNewsCheckResp {
    // 主键ID
    private Long freshNewsCheckId;

    // 关联的新鲜事ID
    private Long freshNewsId;

    // 标题
    private String title;

    // 内容
    private String content;

    // 审核图片URL
    private String imageUrl;

    // 审核状态（0：待审核，1：通过，2：拒绝）
    private Integer checkStatus;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 是否删除
    private Integer isDeleted;

    // 审核时间
    private LocalDateTime checkTime;
}
