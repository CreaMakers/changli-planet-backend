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

    // 序号
    public String id;

    // 开课学期
    public String item;

    // 课程名称
    public String name;

    // 成绩
    public String grade;

    // 成绩标识
    public String flag;

    // 学分
    public String score;

    // 总学时
    public String timeR;

    // 绩点
    public String point;

    // 补重学期
    public String ReItem;

    // 考核方式
    public String method;

    // 考试性质
    public String property;

    // 课程属性
    public String attribute;
}
