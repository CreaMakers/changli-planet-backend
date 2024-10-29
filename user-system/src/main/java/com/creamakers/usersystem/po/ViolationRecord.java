package com.creamakers.usersystem.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户违规记录表
 * @author Hayaizo
 * @date 2024-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("violation_record")
public class ViolationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 违规用户id
    */
    private Integer userId;

    /**
     * 违规行为描述
     */
    private String description;

    /**
    * 违规类型: 1-言论违规，2-行为违规，3-其他
    */
    private Integer violationType;

    /**
    * 处罚类型: 0-无，1-警告，2-封禁，3-禁言
    */
    private Integer penaltyType;

    /**
    * 处罚状态: 0-未处罚， 1-处罚中， 2-处罚完成
    */
    private Integer penaltyStatus;

    /**
    * 违规时间
    */
    private Date violationTime;

    /**
    * 处罚时间
    */
    private Date penaltyTime;

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
   @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
    * 记录更新时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
    * 是否删除: 0-未删除，1-已删除
    */
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

}