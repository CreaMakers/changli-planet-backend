package com.creamakers.websystem.domain.vo.response;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 违规记录响应类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationRecordResp implements Serializable {

    private Long id;
    /**
     * 违规用户ID
     */
    private Integer userId;

    /**
     * 违规类型: 1-言论违规，2-行为违规，3-其他
     */
    private Integer violationType;

    /**
     * 处罚类型: 0-无，1-警告，2-封禁，3-禁言
     */
    private Integer penaltyType;

    /**
     * 处罚状态: 0-未处罚, 1-处罚中, 2-处罚完成
     */
    private Integer penaltyStatus;

    /**
     * 违规时间
     */
    private LocalDateTime violationTime;

    /**
     * 处罚时间
     */
    private LocalDateTime penaltyTime;

    /**
     * 禁言持续时间（分钟）
     */
    private Integer muteDuration;

    /**
     * 封禁持续时间（分钟）
     */
    private Integer banDuration;

    /**
     * 处罚原因
     */
    private String penaltyReason;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    private Byte isDeleted;

    /**
     * 违规行为描述
     */
    private String description;
}
