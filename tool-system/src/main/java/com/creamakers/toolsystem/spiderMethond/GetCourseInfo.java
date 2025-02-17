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
    private String cookie;
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
                .header("Cookie", cookie)
                .data("zc", week)
                .data("xnxq01id", termId);


        res = con.execute();

        Document doc = Jsoup.parse(res.body());
//        System.out.println(doc);
        Elements courseDivs = doc.select("div.kbcontent");


        Element emptyDataElement = doc.selectFirst("td[colspan=10]");
        if (emptyDataElement != null && emptyDataElement.text().contains("未查询到数据")) {

            System.out.println("未查询到成绩数据");

            return new LinkedList<CourseInfo>();
        }


        List<CourseInfo> courseList = new ArrayList<>();


        for (Element div : courseDivs) {
            // 提取课程名称
            String courseName = div.ownText().trim();

            String teacher = null;
            String weeks = null;
            String classroom = null;
            String weekday = null;

            // 获取div的id并解析出星期和第几节课
            String divId = div.attr("id");
            if (divId != null && !divId.isEmpty()) {
                String[] idParts = divId.split("-");
                if (idParts.length >= 2) {
                    // 倒数第二位表示星期
                    weekday = idParts[idParts.length - 2];
                }
            }

            // 查找课程的周次、教室、老师等信息
            Elements fonts = div.select("font");
            for (Element font : fonts) {
                String title = font.attr("title");
                String text = font.text();

                if ("老师".equals(title)) {
                    teacher = text;
                } else if ("周次(节次)".equals(title)) {
                    weeks = text;
                } else if ("教室".equals(title)) {
                    classroom = text;
                }
            }


            if (!courseName.isEmpty()) {
                CourseInfo course = new CourseInfo(courseName, teacher, weeks, classroom, weekday);
                courseList.add(course);
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
