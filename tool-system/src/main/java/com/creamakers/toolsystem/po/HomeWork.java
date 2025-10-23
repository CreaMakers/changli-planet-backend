package com.creamakers.toolsystem.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "作业实体类")
// 指定数据库表名
@TableName("homework")
public class HomeWork {
    @ApiModelProperty(value = "作业ID", example = "1")
    @NotBlank(message = "作业ID不能为空")
    @TableId(type = IdType.AUTO)
    private Integer id;


    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", example = "1")
    @TableField("user_id")
    private Integer userId;


    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 255, message = "用户名长度应在3到255之间")
    @ApiModelProperty(value = "用户名", example = "john_doe")
    @TableField("username")
    private String username;


    @NotBlank(message = "邮箱不能为空")
    @ApiModelProperty(value = "邮箱", example = "123@qq.com")
    @TableField("mailbox")
    private String mailbox;


    @NotBlank(message = "作业名称不能为空")
    @ApiModelProperty(value = "作业名称", example = "大学物理")
    @TableField("home_work_name")
    private String homeWorkName;


    @NotBlank(message = "作业状态不能为空")
    @ApiModelProperty(value = "作业状态: 0-未完成,1-已完成,2-已过期", example = "0")
    @TableField("status")
    private Integer status;


    // 返回 ISO 8601 时间格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    @ApiModelProperty(value = "过期时间", example = "2024-10-22T10:00:00.000+08:00")
    @TableField("expire_time")
    private LocalDateTime expireTime;


    // 返回 ISO 8601 时间格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2024-10-22T10:00:00.000+08:00")
    @TableField("create_time")
    private LocalDate createTime;


    // 返回 ISO 8601 时间格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    @ApiModelProperty(value = "最后更新时间", example = "2024-10-22T12:00:00.000+08:00")
    @TableField("update_time")
    private LocalDate updateTime;


    @NotNull(message = "是否删除字段不能为空")
    @ApiModelProperty(value = "是否删除: 0-未删除,1-已删除", example = "0")
    @TableField("is_deleted")
    private Byte isDeleted;
}
