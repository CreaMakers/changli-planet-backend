package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApkResp {

    // APK版本ID
    private Long id;
    // 版本号
    private Integer versionCode;

    // 版本名称
    private String versionName;

    // 下载链接
    private String downloadUrl;

    // 更新消息
    private String updateMessage;

    // 创建时间
    private LocalDateTime createTime;
}

