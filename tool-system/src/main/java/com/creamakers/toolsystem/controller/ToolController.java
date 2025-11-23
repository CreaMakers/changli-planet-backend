package com.creamakers.toolsystem.controller;

import com.creamakers.toolsystem.dto.request.*;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.dto.response.TopicSkinResponse;
import com.creamakers.toolsystem.entity.*;
import com.creamakers.toolsystem.service.ToolService;
import com.creamakers.toolsystem.service.TopicSkinService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/app/tools")
public class ToolController {

    @Autowired
    private ToolService toolService;

    @Autowired
    private TopicSkinService topicSkinService;

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

    @GetMapping("/courses/data")
    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfo(@RequestParam(value = "stuNum") String stuNum,
                                                                           @RequestParam(value = "password") String password,
                                                                           @RequestParam(value = "data") String data) throws IOException {

        // 创建 CourseInfoRequest 对象，并设置从查询参数中获得的值
        CourseInfoRequest courseInfoRequest = new CourseInfoRequest();
        courseInfoRequest.setStuNum(stuNum);
        courseInfoRequest.setPassword(password);
        courseInfoRequest.setData(data);
        return toolService.GetCourseInfoByData(courseInfoRequest);
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

    @GetMapping("/classroom")
    public ResponseEntity<GeneralResponse<ArrayList<String>>> GetClassroomInfo(@RequestParam(value = "stuNum") String stuNum,
                                                                               @RequestParam(value = "password") String password,
                                                                               @RequestParam(value = "week") String week,
                                                                               @RequestParam(value = "day") String day,
                                                                               @RequestParam(value = "term") String term,
                                                                               @RequestParam(value = "region") String region,
                                                                               @RequestParam(value = "start") String start,
                                                                               @RequestParam(value = "end") String end, Principal principal) throws IOException {
        ClassroomInfoRequest classroomInfoRequest = new ClassroomInfoRequest(
                stuNum, password, week, day, term, region, start, end
        );
        return toolService.GetClassroomInfo(classroomInfoRequest);
    }



    @GetMapping("/grades/detail")
    public ResponseEntity<GeneralResponse<PscjInfo>> getGradesDetailsInfo(
            @RequestParam(value = "stuNum") String stuNum,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "pscjUrl") String pscjUrl) throws IOException, InterruptedException {

        // 调用服务方法，并返回响应
        return toolService.GetGradesDetailInfo(new GradeDetailInfoRequest(stuNum, password, pscjUrl));
    }

    // 作业提醒
    @PostMapping("/homework")
    public ResponseEntity<GeneralResponse> homeWorkRemind(@RequestBody HomeWorkRequest homeWorkRequest) {
        return toolService.homeWorkRemind(homeWorkRequest);
    }

    //上传主题皮肤apk包(内部使用)
    @PostMapping("/skin")
    public ResponseEntity<GeneralResponse<TopicSkinResponse>> uploadSkin(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description){
        return topicSkinService.uploadSkin(file,image,name,description);
    }

    //删除主题皮肤apk包(内部使用)
    @DeleteMapping("/skin")
    public ResponseEntity<GeneralResponse> deleteSkin(@RequestParam(value = "id",required = false) Integer id,
                                                      @RequestParam(value = "name",required = false) String name){
        if(id == null && StringUtils.isBlank(name)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GeneralResponse
                            .builder()
                            .code("400")
                            .msg("id或name参数不能全为空")
                            .build());
        }
        return topicSkinService.deleteSkin(id,name);
    }

    //获取主题皮肤apk包 (? 参数避免了返回类型不匹配的问题)
    @GetMapping("/skin")
    public ResponseEntity<?> getSkin(@RequestParam(value = "id",required = false) Integer id,
                                     @RequestParam(value = "name",required = false) String name){
        if(id == null && StringUtils.isBlank(name)){
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("400", "资源不存在",null));
        }
        return topicSkinService.getSkin(id,name);
    }

    //展示所有主题皮肤
    @GetMapping("/skin/list")
    public ResponseEntity<GeneralResponse<List<TopicSkinResponse>>> getAllSkin(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return topicSkinService.getAllSkin(page,pageSize);
    }

//    @PostMapping("/getWeekDate")
//    public ResponseEntity<GeneralResponse> getWeekDate(
//            @RequestParam(value = "stuNum") String stuNum,
//            @RequestParam(value = "password") String password
//    ) {
//        WeekDateRequest weekDateRequest = new WeekDateRequest(stuNum, password);
//        return toolService.getWeekDate(weekDateRequest);
//    }

}
