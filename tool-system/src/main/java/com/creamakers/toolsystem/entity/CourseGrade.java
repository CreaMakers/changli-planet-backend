package com.creamakers.toolsystem.entity;


import com.baomidou.mybatisplus.annotation.TableName;
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
@ApiModel(description = "课程成绩实体类")
public class CourseGrade {
    public String id; // 序号
    public String item;  // 开课学期
    public String name; // 课程名称
    public String grade; // 成绩
    public String flag; // 成绩标识
    public String score; // 学分
    public String timeR; // 总学时
    public String point; // 绩点
    public String ReItem; // 补重学期
    public String method; // 考核方式
    public String property; // 考试性质
    public String attribute; // 课程属性
}
