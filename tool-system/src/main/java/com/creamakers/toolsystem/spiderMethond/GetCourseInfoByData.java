package com.creamakers.toolsystem.spiderMethond;

import com.creamakers.toolsystem.entity.CourseInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetCourseInfoByData {
    private final String url = "http://xk.csust.edu.cn/jsxsd/framework/main_index_loadkb.jsp";
    private String cookie;
    private Connection con;
    private Connection.Response res;

    public GetCourseInfoByData(String cookies) throws IOException {
        // 将传入的 cookies 与 getJwCode()[2] 拼接
        String combinedCookies = cookies;

        // 使用正则表达式提取 "JSESSIONID" 和 "SERVERID_jsxsd" 相关的部分
        StringBuilder retainedCookies = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(JSESSIONID=[^;]*|SERVERID_jsxsd=[^;]*)").matcher(combinedCookies);
        while (matcher.find()) {
            // 添加每个匹配到的部分，并加上分号分隔
            retainedCookies.append(matcher.group()).append("; ");
        }

        // 去除末尾多余的 "; " 并将结果赋值给 this.cookie
        this.cookie = retainedCookies.toString().trim();
    }

    // 获取课表数据
    public List<CourseInfo> getData(String data) throws IOException {

        // 使用Jsoup连接请求
        con = Jsoup.connect(url)
                .followRedirects(false)
                .method(Connection.Method.GET)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookie)   // 使用传入的String类型的Cookie
                .data("rq", data);  // 查询日期

        // 发送请求并获取响应
        res = con.execute();
        // 解析HTML
        Document doc = Jsoup.parse(res.body());
//        System.out.println(doc);
        Elements courseParagraphs = doc.select("p[title]");// 选择class为kbcontent的<div>元素
        String docText = doc.toString();

        if (docText.contains("当前日期不在教学周历内")) {
            System.out.println("当前日期不在教学周历内");
            return new ArrayList<>();
        }
        // 存储课程信息
        List<CourseInfo> courseList = new ArrayList<>();

// 遍历所有找到的课程div元素
        for (Element p : courseParagraphs) {
            String titleAttr = p.attr("title"); // 获取完整的title属性内容

            // 解析title属性中的信息
            String[] lines = titleAttr.split("<br/>");
            String courseName = "";
            String classroom = "";
            String time = "";

            for (String line : lines) {
                if (line.contains("课程名称：")) {
                    courseName = line.replace("课程名称：", "").trim();
                }
                if (line.contains("上课地点：")) {
                    classroom = line.replace("上课地点：", "").trim();
                }
                if (line.contains("上课时间：")) {
                    time = line.replace("上课时间：", "").trim();
                }
            }

            // 从时间信息中提取星期
            String weekday = "";
            if (time.contains("星期")) {
                weekday = time.substring(time.indexOf("星期"), time.indexOf("星期") + 3);
            }

            // 如果课程名称不为空,创建课程对象
            if (!courseName.isEmpty()) {
                CourseInfo course = new CourseInfo(courseName, "", time, classroom, weekday);
                courseList.add(course);
            }
        }



        return courseList;
    }
}
