package com.creamakers.toolsystem.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "作业请求实体类")
public class HomeWorkRequest {
    // 用户名
    @JsonProperty("username")
    private String username;
    // 作业名称
    @JsonProperty("homework_name")
    private String homeworkName;
    // 作业状态
    @JsonProperty("status")
    private Integer status;
    // 截止时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @JsonProperty("end_time")
    private LocalDateTime endTime;
}
