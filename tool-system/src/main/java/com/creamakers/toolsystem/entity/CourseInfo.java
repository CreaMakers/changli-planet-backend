package com.creamakers.toolsystem.entity;

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
@ApiModel(description = "课程信息实体类")
public class CourseInfo {

    // 课程名称
    private String courseName;

    // 课程老师
    private String teacher;

    // 上课周次
    private String weeks;

    // 课程地点
    private String classroom;

    // 上课星期
    private String weekday;
}
