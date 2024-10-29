package com.creamakers.toolsystem.controller;

import com.creamakers.toolsystem.dto.request.CourseInfoRequest;
import com.creamakers.toolsystem.dto.request.ElectricityChargeRequest;
import com.creamakers.toolsystem.dto.request.ExamArrangeInfoRequest;
import com.creamakers.toolsystem.dto.request.GradesInfoRequest;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.entity.CourseGrade;
import com.creamakers.toolsystem.entity.CourseInfo;
import com.creamakers.toolsystem.entity.ExamArrange;
import com.creamakers.toolsystem.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/app/tools")
public class ToolController {

   @Autowired
   private ToolService toolService;

    @GetMapping("/courses")
    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfo(@RequestBody CourseInfoRequest courseInfoRequest) throws IOException {
        return toolService.GetCourseInfo(courseInfoRequest);
    }

    @GetMapping("/grades")
    public ResponseEntity<GeneralResponse<List<CourseGrade>>> GetGradesInfo(@RequestBody GradesInfoRequest gradesInfoRequest) throws IOException {
        return toolService.GetGradesInfo(gradesInfoRequest);
    }

    @GetMapping("/exams")
    public ResponseEntity<GeneralResponse<List<ExamArrange>>> GetExamArrangeInfo(@RequestBody ExamArrangeInfoRequest examArrangeInfoRequest) throws IOException {
        return toolService.GetExamArrangeInfo(examArrangeInfoRequest);
    }

    @GetMapping("/dormitory-electricity")
    public ResponseEntity<GeneralResponse> GetElectricityChargeInfo(@RequestBody ElectricityChargeRequest electricityChargeRequest) throws IOException {
        return toolService.GetElectricityChargeInfo(electricityChargeRequest);
    }



}
