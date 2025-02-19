package com.creamakers.websystem.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("apk_updates")
public class ApkUpdate {

    // 记录ID
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 版本代码
    @TableField(value = "version_code")
    private Integer versionCode;

    // 版本名称
    @TableField(value = "version_name")
    private String versionName;

    // 下载链接
    @TableField(value = "download_url")
    private String downloadUrl;

    // 更新信息
    @TableField(value = "update_message")
    private String updateMessage;

    // 记录创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    // 记录更新时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
