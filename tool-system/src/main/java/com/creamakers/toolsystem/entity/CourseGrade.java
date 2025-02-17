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
    private String id;

    // 开课学期
    private String item;

    // 课程名称
    public String name;

    // 成绩
    private String grade;

    // 成绩标识
    private String flag;

    // 学分
    private String score;

    // 总学时
    private String timeR;

    // 绩点
    private String point;

    // 补重学期
    private String ReItem;

    // 考核方式
    private String method;

    // 考试性质
    private String property;

    // 课程属性
    private String attribute;

    /*
     上机成绩比例
     */
    private String pscjUrl;

    private String cookie;
}
