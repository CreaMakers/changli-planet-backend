package com.creamakers.toolsystem.spiderMethond;


import com.creamakers.toolsystem.entity.CourseGrade;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetCourseGrade {

    //从教务系统获取code的url
    private static final String GET_CODE_URL = "http://xk.csust.edu.cn/Logon.do?method=logon&flag=sess";
    // 成绩查询的URL
    private final String url = "http://xk.csust.edu.cn/jsxsd/kscj/cjcx_list";
    private final String cookie;

    // 构造函数，接受 Cookie 作为 String 类型
    public GetCourseGrade(String cookie) {
        // 将传入的 cookies 与 getJwCode()[2] 拼接
        String combinedCookies = cookie;

        // 使用正则表达式提取 "JSESSIONID" 和 "SERVERID_jsxsd" 相关的部分
        StringBuilder retainedCookies = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(JSESSIONID=[^;]*|SERVERID_jsxsd=[^;]*)").matcher(combinedCookies);
        while (matcher.find()) {
            // 添加每个匹配到的部分，并加上分号分隔
            retainedCookies.append(matcher.group()).append("; ");
        }

        this.cookie = retainedCookies.toString().trim();
    }

    // 获取成绩数据
    public List<CourseGrade> getData(String term) throws IOException {

        // 使用 Jsoup 连接请求，设置 Cookie 和查询参数
        Connection.Response res = Jsoup.connect(url)
                .followRedirects(false)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookie)
                // 表单数据提交
                .data("kksj", term)
                .data("kcxz", "")
                .data("kcmc", "")
                .data("xsfs", "all")
                .data("fxkc", "2")
                .execute();


        if (res.statusCode() != 200) {
            throw new IOException("获取成绩数据失败，HTTP 状态码: " + res.statusCode());
        }


        Document doc = Jsoup.parse(res.body());

        Elements rows = doc.select("#dataList > tbody > tr");


        List<CourseGrade> gradeList = new ArrayList<>();


        for (Element row : rows) {
            Elements cols = row.select("td");

            if (cols.size() < 13) {
                continue;
            }


            String score = cols.get(5).select("a").isEmpty() ? cols.get(5).text() : cols.get(5).select("a").text();


            CourseGrade grade = new CourseGrade(
                    cols.get(0).text(),  // 序号
                    cols.get(1).text(),  // 开课学期
                    cols.get(3).text(),  // 课程名称
                    score,  // 成绩，可能是链接
//                    cols.get(6).text(),  // 成绩标识
//                    cols.get(7).text(),  // 学分
//                    cols.get(8).text(),  // 总学时
//                    cols.get(9).text(),  // 绩点
//                    cols.get(10).text(),  // 补重学期
//                    cols.get(11).text(),  // 考核方式
//                    cols.get(12).text(),  // 考试性质
//                    cols.get(13).text()  // 课程属性
                    cols.get(7).text(),  // 成绩标识
                    cols.get(8).text(),  // 学分
                    cols.get(9).text(),  // 总学时
                    cols.get(10).text(),  // 绩点
                    cols.get(11).text(),  // 补重学期
                    cols.get(12).text(),  // 考核方式
                    cols.get(13).text(),  // 考试性质
                    cols.get(14).text()  // 课程属性
            );

            // 将成绩对象添加到列表中
            gradeList.add(grade);
        }

        return gradeList;
    }

}