package com.creamakers.toolsystem.spiderMethond;


import com.creamakers.toolsystem.entity.CourseInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 获取课表
public class GetCourseInfo {

    //从教务系统获取code的url
    private static final String GET_CODE_URL = "http://xk.csust.edu.cn/Logon.do?method=logon&flag=sess";
    // 教务系统课表URL
    private final String url = "http://xk.csust.edu.cn/jsxsd/xskb/xskb_list.do";
    private  String cookie;
    private Connection con;
    private Connection.Response res;

    public GetCourseInfo(String cookies) throws IOException {
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
    public List<CourseInfo> getData(String week, String termId) throws IOException {

        // 使用Jsoup连接请求
        con = Jsoup.connect(url)
                .followRedirects(false)
                .method(Connection.Method.GET)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookie)   // 使用传入的String类型的Cookie
                .data("zc", week)   // 周次
                .data("xnxq01id", termId);  // 学年学期ID

        // 发送请求并获取响应
        res = con.execute();
        // 解析HTML
        Document doc = Jsoup.parse(res.body());
//        System.out.println(doc);
        Elements courseDivs = doc.select("div.kbcontent");// 选择class为kbcontent的<div>元素


        // 检查是否存在数据
        Element emptyDataElement = doc.selectFirst("td[colspan=10]");  // 查找显示“未查询到数据”的 <td> 元素
        if (emptyDataElement != null && emptyDataElement.text().contains("未查询到数据")) {
            // 如果未找到数据，返回空列表或抛出异常
            System.out.println("未查询到成绩数据");

            return new LinkedList<CourseInfo>();
        }

        // 存储课程信息
        List<CourseInfo> courseList = new ArrayList<>();

// 遍历所有找到的课程div元素
        for (Element div : courseDivs) {
            String courseName = div.ownText().trim();  // 提取课程名称

            String teacher = null;
            String weeks = null;
            String classroom = null;
            String weekday = null;

            // 获取div的id并解析出星期和第几节课
            String divId = div.attr("id");  // 获取div的id
            if (divId != null && !divId.isEmpty()) {
                String[] idParts = divId.split("-");
                if (idParts.length >= 2) {
                    weekday = idParts[idParts.length - 2];  // 倒数第二位表示星期
                }
            }

            // 查找课程的周次、教室、老师等信息
            Elements fonts = div.select("font");  // 获取所有<font>标签
            for (Element font : fonts) {
                String title = font.attr("title");
                String text = font.text();

                if (title.equals("老师")) {
                    teacher = text;  // 老师信息
                } else if (title.equals("周次(节次)")) {
                    weeks = text;  // 周次节次信息
                } else if (title.equals("教室")) {
                    classroom = text;  // 教室信息
                }
            }

            // 如果课程名称不为空，则创建courseInfo对象
            if (!courseName.isEmpty()) {
                CourseInfo course = new CourseInfo(courseName, teacher, weeks, classroom, weekday);
                courseList.add(course);  // 将课程添加到列表中
            }
        }




        return courseList;
    }

    // 将字符串按每5个字符插入换行符
    public String spl(String str) {
        StringBuilder s = new StringBuilder(str);
        for (int index = 0; index < s.length(); index += 5) {
            s.insert(index, "\n");
        }
        return s.toString();
    }
}
