package com.creamakers.toolsystem.controller;

import com.creamakers.toolsystem.dto.request.*;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.entity.CourseGrade;
import com.creamakers.toolsystem.entity.CourseInfo;
import com.creamakers.toolsystem.entity.ExamArrange;
import com.creamakers.toolsystem.entity.PscjInfo;
import com.creamakers.toolsystem.service.ToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/app/tools")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @GetMapping("/courses")
    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfo(@RequestParam(value = "stuNum") String stuNum,
                                                                           @RequestParam(value = "password") String password,
                                                                           @RequestParam(value = "week") String week,
                                                                           @RequestParam(value = "termId") String termId) throws IOException {

        // 创建 CourseInfoRequest 对象，并设置从查询参数中获得的值
        CourseInfoRequest courseInfoRequest = new CourseInfoRequest();
        courseInfoRequest.setStuNum(stuNum);
        courseInfoRequest.setPassword(password);
        courseInfoRequest.setWeek(week);
        courseInfoRequest.setTermId(termId);
        return toolService.GetCourseInfo(courseInfoRequest);
    }


    @GetMapping("/grades")
    public ResponseEntity<GeneralResponse<List<CourseGrade>>> getGradesInfo(
            @RequestParam(value = "stuNum") String stuNum,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "term") String term) throws IOException {

        // 创建 GradesInfoRequest 对象，并设置从查询参数中获得的值
        GradesInfoRequest gradesInfoRequest = new GradesInfoRequest();
        gradesInfoRequest.setStuNum(stuNum);
        gradesInfoRequest.setPassword(password);
        gradesInfoRequest.setTerm(term);

        // 调用服务方法，并返回响应
        return toolService.GetGradesInfo(gradesInfoRequest);
    }
    @GetMapping("/exams")
    public ResponseEntity<GeneralResponse<List<ExamArrange>>> getExamArrangeInfo(
            @RequestParam(value = "stuNum") String stuNum,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "term") String term,
            @RequestParam(value = "examType") String examType) throws IOException {

        // 创建 ExamArrangeInfoRequest 对象，并设置从查询参数中获得的值
        ExamArrangeInfoRequest examArrangeInfoRequest = new ExamArrangeInfoRequest();
        examArrangeInfoRequest.setStuNum(stuNum);
        examArrangeInfoRequest.setPassword(password);
        examArrangeInfoRequest.setTerm(term);
        examArrangeInfoRequest.setExamType(examType);

        return toolService.GetExamArrangeInfo(examArrangeInfoRequest);
    }
    @GetMapping("/dormitory-electricity")
    public ResponseEntity<GeneralResponse> GetElectricityChargeInfo(@RequestParam(value = "address") String address,
                                                                    @RequestParam(value = "buildId") String buildId,
                                                                    @RequestParam(value = "nod") String nod) throws IOException {
        ElectricityChargeRequest electricityChargeRequest = new ElectricityChargeRequest();
        electricityChargeRequest.setAddress(address);
        electricityChargeRequest.setBuildId(buildId);
        electricityChargeRequest.setNod(nod);
        return toolService.GetElectricityChargeInfo(electricityChargeRequest);
    }
    //平时成绩
//    @GetMapping("/queryPscj")
//    public ResponseEntity<GeneralResponse<PscjInfo>> queryUsualGrades(@RequestParam(value = "stuNum") String stuNum,
//                                                                      @RequestParam(value = "password") String password,
//                                                                      @RequestParam(value = "pscjUrl") String pscjUrl) throws IOException {
//        PscjInfoRequest pscjInfoRequest=new PscjInfoRequest();
//        pscjInfoRequest.setStuNum(stuNum);
//        pscjInfoRequest.setPassword(password);
//        pscjInfoRequest.setPscjUrl(pscjUrl);
//        return toolService.getScoreDetail(pscjInfoRequest);
//    }

}
