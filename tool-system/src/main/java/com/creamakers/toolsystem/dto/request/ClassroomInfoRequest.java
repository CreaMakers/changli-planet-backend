package com.creamakers.toolsystem.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "教室请求实体类")
public class ClassroomInfoRequest {
    private String stuNum;
    private String password;
    private String week; // 查询周次
    private String day; // 查询星期几
    private String term; // 学期
    private String region; // 校区 1云塘 2金村
    private String start; // 开始节次 从01 到 10
    private String end; // 结束周次 从01 到 10
}
