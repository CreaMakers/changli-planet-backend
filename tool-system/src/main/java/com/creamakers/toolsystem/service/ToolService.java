package com.creamakers.toolsystem.service;

import com.creamakers.toolsystem.consts.HttpCode;
import com.creamakers.toolsystem.dto.request.CourseInfoRequest;
import com.creamakers.toolsystem.dto.request.ElectricityChargeRequest;
import com.creamakers.toolsystem.dto.request.ExamArrangeInfoRequest;
import com.creamakers.toolsystem.dto.request.GradesInfoRequest;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.entity.CourseGrade;
import com.creamakers.toolsystem.entity.CourseInfo;
import com.creamakers.toolsystem.entity.ElectricityCharge;
import com.creamakers.toolsystem.entity.ExamArrange;
import com.creamakers.toolsystem.spiderMethond.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


import com.creamakers.toolsystem.consts.SuccessMessage;
import com.creamakers.toolsystem.consts.ErrorMessage;


@Service
public class ToolService {

    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfo(CourseInfoRequest courseInfoRequest) throws IOException {

        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(courseInfoRequest.getStuNum(), courseInfoRequest.getPassword());

        GetCourseInfo getCourseInfo = new GetCourseInfo(cook);

        // 获取课程信息，通过链式调用设置cookie
        List<CourseInfo> courses = getCourseInfo.getData(courseInfoRequest.getWeek(), courseInfoRequest.getTermId());

        if (courses == null || courses.isEmpty()) {
            // 如果课程列表为空，返回404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<List<CourseInfo>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_COURSES_FOUND)
                            .data(null)
                            .build());
        }

        // 返回成功响应
        return ResponseEntity.ok(
                GeneralResponse.<List<CourseInfo>>builder()
                        .code(HttpCode.OK)
                        .msg(SuccessMessage.COURSES_RETRIEVED_SUCCESSFULLY)
                        .data(courses)
                        .build()
        );
    }

    public ResponseEntity<GeneralResponse<List<CourseGrade>>> GetGradesInfo(GradesInfoRequest gradesInfoRequest) throws IOException {
        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(gradesInfoRequest.getStuNum(), gradesInfoRequest.getPassword());

        GetCourseGrade getCourseGrade = new GetCourseGrade(cook);

        List<CourseGrade> courseGrades = getCourseGrade.getData(gradesInfoRequest.getTerm());

        if (courseGrades == null || courseGrades.isEmpty()) {
            // 如果成绩列表为空，返回404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<List<CourseGrade>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_GRADES_FOUND)
                            .data(null)
                            .build());
        }

        // 返回成功响应
        return ResponseEntity.ok(
                GeneralResponse.<List<CourseGrade>>builder()
                        .code(HttpCode.OK)
                        .msg(SuccessMessage.GRADES_RETRIEVED_SUCCESSFULLY)
                        .data(courseGrades)
                        .build()
        );
    }

    public ResponseEntity<GeneralResponse<List<ExamArrange>>> GetExamArrangeInfo(ExamArrangeInfoRequest examArrangeRequest) throws IOException {
        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(examArrangeRequest.getStuNum(), examArrangeRequest.getPassword());

        GetExamArrange getExamArrange = new GetExamArrange(cook);
        List<ExamArrange> examArranges = getExamArrange.getData(examArrangeRequest.getTerm(), examArrangeRequest.getExamType());

        if (examArranges == null || examArranges.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<List<ExamArrange>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_EXAM_ARRANGEMENTS_FOUND)
                            .data(null)
                            .build());
        }

        return ResponseEntity.ok(
                GeneralResponse.<List<ExamArrange>>builder()
                        .code(HttpCode.OK)
                        .msg(SuccessMessage.EXAM_ARRANGEMENTS_RETRIEVED_SUCCESSFULLY)
                        .data(examArranges)
                        .build()
        );
    }


    public ResponseEntity<GeneralResponse> GetElectricityChargeInfo(ElectricityChargeRequest electricityChargeRequest) {
        // 调用 ElectricityChargeService 获取电费信息
        GetElectricityCharge electricityChargeService = new GetElectricityCharge();

        ElectricityCharge chargeInfo = electricityChargeService.getCharge(
                electricityChargeRequest.getAddress(),
                electricityChargeRequest.getBuildId(),
                electricityChargeRequest.getNod()
        );

        // 获取电量查询的消息
        String msg =  chargeInfo.getMsg();
        // 构建 GeneralResponse 响应对象
        GeneralResponse<Void> response = GeneralResponse.<Void>builder()
                .code(HttpCode.OK) // 设置状态码
                .msg(msg)    // 设置消息
                .data(null)  // 设置数据为 null，因为不需要返回具体数据
                .build();

        return ResponseEntity.ok(response);
    }
}
