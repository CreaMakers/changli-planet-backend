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
public class ExamArrange {
    public int id;         // 序号
    public String place;  // 校区
    public String examId;  // 考试场次
    public String CourseId; // 课程编号
    public String name; // 课程名称
    public String teacher; // 授课老师
    public String time; //  考试时间
    public String room; // 考场
}