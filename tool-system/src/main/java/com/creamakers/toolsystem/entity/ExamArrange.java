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
@ApiModel(description = "考试安排实体类")
public class ExamArrange {

    // 序号
    public int id;

    // 校区
    public String place;

    // 考试场次
    public String examId;

    // 课程编号
    public String CourseId;

    // 课程名称
    public String name;

    // 授课老师
    public String teacher;

    // 考试时间
    public String time;

    // 考场
    public String room;
}
