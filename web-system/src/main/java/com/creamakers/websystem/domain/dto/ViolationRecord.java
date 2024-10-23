package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户违规记录实体类
 *
 * @author dcelysia
 * @since 2024-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("violation_record")
public class ViolationRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 违规用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 违规类型: 1-言论违规，2-行为违规，3-其他
     */
    @TableField("violation_type")
    private Integer violationType;

    /**
     * 处罚类型: 0-无，1-警告，2-封禁，3-禁言
     */
    @TableField("penalty_type")
    private Integer penaltyType;

    /**
     * 处罚状态: 0-未处罚, 1-处罚中, 2-处罚完成
     */
    @TableField("penalty_status")
    private Integer penaltyStatus;

    /**
     * 违规时间
     */
    @TableField("violation_time")
    private LocalDateTime violationTime;

    /**
     * 处罚时间
     */
    @TableField("penalty_time")
    private LocalDateTime penaltyTime;

    /**
     * 禁言持续时间（分钟）
     */
    @TableField("mute_duration")
    private Integer muteDuration;

    /**
     * 封禁持续时间（分钟）
     */
    @TableField("ban_duration")
    private Integer banDuration;

    /**
     * 处罚原因
     */
    @TableField("penalty_reason")
    private String penaltyReason;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除: 0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Byte isDeleted;

    /**
     * 违规行为描述
     */
    @TableField("description")
    private String description;
}