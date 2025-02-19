package com.creamakers.fresh.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 标签DTO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tags")
public class Tags {

    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    // 标签名称
    @TableField(value = "name")
    private String name;

    // 是否删除: 0-未删除, 1-已删除
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    // 创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
