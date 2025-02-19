package com.creamakers.toolsystem.service;

import com.creamakers.toolsystem.consts.HttpCode;
import com.creamakers.toolsystem.dto.request.*;
import com.creamakers.toolsystem.dto.response.GeneralResponse;
import com.creamakers.toolsystem.entity.CourseGrade;
import com.creamakers.toolsystem.entity.CourseInfo;
import com.creamakers.toolsystem.entity.ElectricityCharge;
import com.creamakers.toolsystem.entity.ExamArrange;
import com.creamakers.toolsystem.spiderMethond.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.creamakers.toolsystem.consts.SuccessMessage;
import com.creamakers.toolsystem.consts.ErrorMessage;


@Service
public class ToolService {

    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfo(CourseInfoRequest courseInfoRequest) throws IOException {

        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(courseInfoRequest.getStuNum(), courseInfoRequest.getPassword());

        if (cook == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GeneralResponse.<List<CourseInfo>>builder()
                            .code(HttpCode.FORBIDDEN)
                            .msg(ErrorMessage.INCORRECT_USER)
                            .data(null)
                            .build());
        }
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

    public ResponseEntity<GeneralResponse<List<CourseInfo>>> GetCourseInfoByData(CourseInfoRequest courseInfoRequest) throws IOException {

        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(courseInfoRequest.getStuNum(), courseInfoRequest.getPassword());
        if (cook == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GeneralResponse.<List<CourseInfo>>builder()
                            .code(HttpCode.FORBIDDEN)
                            .msg(ErrorMessage.INCORRECT_USER)
                            .data(null)
                            .build());
        }
        GetCourseInfoByData getCourseInfo = new GetCourseInfoByData(cook);

        // 获取课程信息，通过链式调用设置cookie
        List<CourseInfo> courses = getCourseInfo.getData(courseInfoRequest.data);

        if (courses == null || courses.isEmpty()) {
            // 如果课程列表为空，返回404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<List<CourseInfo>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.INCORRECT_DATA)
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

        if (cook == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GeneralResponse.<List<CourseGrade>>builder()
                            .code(HttpCode.FORBIDDEN)
                            .msg(ErrorMessage.INCORRECT_USER)
                            .data(null)
                            .build());
        }

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

        if (cook == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GeneralResponse.<List<ExamArrange>>builder()
                            .code(HttpCode.FORBIDDEN)
                            .msg(ErrorMessage.INCORRECT_USER)
                            .data(null)
                            .build());
        }

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

    public ResponseEntity<GeneralResponse<ArrayList<String>>> GetClassroomInfo(ClassroomInfoRequest classroomInfoRequest) throws IOException {
        GetCookies getCookies = new GetCookies();
        String cook = getCookies.getHeaderFromJW(classroomInfoRequest.getStuNum(), classroomInfoRequest.getPassword());

        if (cook == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GeneralResponse.<ArrayList<String>>builder()
                            .code(HttpCode.FORBIDDEN)
                            .msg(ErrorMessage.INCORRECT_USER)
                            .data(null)
                            .build());
        }

        String combinedCookies = cook;
        StringBuilder retainedCookies = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(JSESSIONID=[^;]*|SERVERID_jsxsd=[^;]*)").matcher(combinedCookies);
        while (matcher.find()) {
            retainedCookies.append(matcher.group()).append("; ");
        }
        cook = retainedCookies.toString().trim();
        Connection con = Jsoup.connect("http://xk.csust.edu.cn/jsxsd/kbxx/jsjy_query2")
                .followRedirects(false)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cook)   // 设置传入的Cookie
                .data("xnxqh", classroomInfoRequest.getTerm()) // 学期
                .data("typewhere", "jszq")
                .data("xqbh", classroomInfoRequest.getRegion()) // 校区
                .data("jszt", "8") // 教室状态
                .data("zc", classroomInfoRequest.getWeek()) // 开始周次
                .data("zc2", classroomInfoRequest.getWeek()) // 结束周次
                .data("xq", classroomInfoRequest.getDay()) // 开始星期
                .data("xq2", classroomInfoRequest.getDay()) // 结束星期
                .data("jc", classroomInfoRequest.getStart()) // 开始节次
                .data("jc2", classroomInfoRequest.getEnd()); // 结束节次

        Connection.Response res = null;
        try {
            res = con.execute();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<ArrayList<String>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_CLASSROOMS_FOUND)
                            .data(null)
                            .build());
        }
        String body = res.body();
        if (body == null || body.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<ArrayList<String>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_CLASSROOMS_FOUND)
                            .data(null)
                            .build());
        }
// 解析HTML
        Document doc = Jsoup.parse(res.body());

// 创建ArrayList存储教室名称
        ArrayList<String> classroomNames = new ArrayList<>();

// 使用Jsoup选择器找到所有包含教室名称的td元素
        Elements rows = doc.select("tr[onmouseover*='cursor']");

        for (Element row : rows) {
            String fullText = row.select("td").first().text();
            // 使用正则表达式提取教室名称（在括号前的部分）
            Pattern pattern = Pattern.compile("(.*?)\\(");
            Matcher m = pattern.matcher(fullText);
            if (m.find()) {
                String classroomName = m.group(1).trim();
                // 去掉checkbox部分
                classroomName = classroomName.replaceAll("^\\s*\\S+\\s+", "");
                // 添加到ArrayList
                classroomNames.add(classroomName);
            }
        }
        if (classroomNames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GeneralResponse.<ArrayList<String>>builder()
                            .code(HttpCode.NOT_FOUND)
                            .msg(ErrorMessage.NO_CLASSROOMS_FOUND)
                            .data(null)
                            .build());
        }
        GeneralResponse<ArrayList<String>> response = GeneralResponse.<ArrayList<String>>builder()
                .code(HttpCode.OK)
                .msg("查询成功")
                .data(classroomNames)
                .build();

        return ResponseEntity.ok(response);
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
        String msg = chargeInfo.getMsg();
        // 构建 GeneralResponse 响应对象
        GeneralResponse<Void> response = GeneralResponse.<Void>builder()
                .code(HttpCode.OK)
                .msg(msg)
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

//    public ResponseEntity<GeneralResponse<List<Integer>>> getWeekDate(WeekDateRequest weekDateRequest) throws IOException {
//        GetCookies getCookies = new GetCookies();
//        String cook = getCookies.getHeaderFromJW(weekDateRequest.getStuNum(), weekDateRequest.getPassword());
//
//        if (cook == null) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(GeneralResponse.<List<Integer>>builder()
//                            .code(HttpCode.FORBIDDEN)
//                            .msg(ErrorMessage.INCORRECT_USER)
//                            .data(null)
//                            .build());
//        }
//
//        String htmlText;
//        htmlText = getWeekDatePage(cook);
//        if (htmlText == null || htmlText.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(GeneralResponse.<List<Integer>>builder()
//                            .code(HttpCode.NOT_FOUND)
//                            .msg(ErrorMessage.NO_EXAM_ARRANGEMENTS_FOUND)
//                            .data(null)
//                            .build());
//        }
//
//        List<Integer> nowWeekDate = parseWeekDateHtml(htmlText);
//
//        if (nowWeekDate == null) {
//            nowWeekDate = new ArrayList<>();
//            nowWeekDate.add(1);
//            nowWeekDate.add(20);
//        }
//
//        Date date = new Date();
//        String weekDay = weekDaySdf.format(date);
//        Integer weekIdByStr = WeekDayEnum.getWeekIdByStr(weekDay);
//        int sub = 1 - weekIdByStr;
//        String dayOfWeekMon = CalendarUtil.getDateOfDesignDay(date, daySdf, sub);
//        Date monDate;
//        synchronized (daySdf) {
//            try {
//                monDate = daySdf.parse(dayOfWeekMon);
//            } catch (ParseException e) {
//                throw new BaseException(CodeEnum.SYSTEM_ERROR.getCode(), "系统错误，获取周数失败");
//            }
//        }
//        List<WeekDay> weekDays = new ArrayList<>();
//        int index = 0;
//
//        for (int i = nowWeekDate.get(0); i >= 1; i--) {
//            WeekDay weekDayTemp = new WeekDay();
//            weekDayTemp.setWeekId(i);
//            weekDayTemp.setWeekMonStr(CalendarUtil.getDateOfDesignDay(monDate, daySdf, index * -1 * 7));
//            index++;
//            weekDays.add(weekDayTemp);
//        }
//        index = 1;
//        for (int i = nowWeekDate.get(0) + 1; i <= nowWeekDate.get(1); i++) {
//            WeekDay weekDayTemp = new WeekDay();
//            weekDayTemp.setWeekId(i);
//            weekDayTemp.setWeekMonStr(CalendarUtil.getDateOfDesignDay(monDate, daySdf, index * 7));
//            index++;
//            weekDays.add(weekDayTemp);
//        }
//        Collections.sort(weekDays);
//        return ReturnData.success(weekDays);
//    }

//    public List<Integer> parseWeekDateHtml(String weekDateHtml) {
//        try {
//            List<Integer> nowWeekDate = new ArrayList<>();
//            Document document = Jsoup.parse(weekDateHtml);
//            Element body = document.body();
//            Element liShowWeekDiv = body.getElementById("li_showWeek");
//            Elements nowWeekDiv = body.getElementsByClass("main_text main_color");
//
//            int nowWeek;
//            try {
//                nowWeek = Integer.parseInt(nowWeekDiv.text().substring(1, nowWeekDiv.text().length() - 1));
//            } catch (Exception e) {
//                throw new BaseException(CodeEnum.SYSTEM_ERROR.getCode(), "教务系统无响应！");
//                //nowWeek = -1;
//            }
//            int totalWeek;
//            try {
//                String totalWeekStr = liShowWeekDiv.text().split("/")[1];
//                totalWeek = Integer.parseInt(totalWeekStr.substring(0, totalWeekStr.length() - 1));
//            } catch (Exception e) {
//                totalWeek = 20;
//            }
//
//            nowWeekDate.add(nowWeek);
//            nowWeekDate.add(totalWeek);
//
//            return nowWeekDate;
//        } catch (Exception e) {
////            throw new BaseException(CodeEnum.PARSE_ERROR,"解析周时间失败");
//            return null;
//        }
//    }

    public String getWeekDatePage(String cookie) throws IOException {
        String url = "http://xk.csust.edu.cn/jsxsd/framework/xsMain_new.jsp?t1=1";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request xsMainRequest = new Request.Builder()
                .header("Cookie", cookie)
                .header("Referer", "http://xk.csust.edu.cn/jsxsd/framework/xsMain.jsp")
                .url(url)
                .build();

        Response response = null;
        response = okHttpClient.newCall(xsMainRequest).execute();
        String htmlText = Objects.requireNonNull(response.body()).string();
        response.close();
        return htmlText;
    }

}
