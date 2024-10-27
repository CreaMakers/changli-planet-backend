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
    private final String url = "http://xk.csust.edu.cn/jsxsd/kscj/cjcx_list";  // 正确的URL地址
    private final String cookie;  // Cookie 字符串

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
                .method(Connection.Method.POST)  // 使用POST方法
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookie)   // 使用传入的 String 类型的 Cookie
                // 表单数据提交
                .data("kksj", term)  // 传递学期参数
                .data("kcxz", "")  // 课程性质为空
                .data("kcmc", "")  // 课程名称为空
                .data("xsfs", "all")  // 修读方式为 all
                .data("fxkc", "2")  // 课程类型
                .execute();

        // 检查响应状态码是否为 200
        if (res.statusCode() != 200) {
            throw new IOException("获取成绩数据失败，HTTP 状态码: " + res.statusCode());
        }

        // 解析 HTML 响应
        Document doc = Jsoup.parse(res.body());

        Elements rows = doc.select("#dataList > tbody > tr");  // 选择成绩表格中的每一行

        // 存储成绩信息
        List<CourseGrade> gradeList = new ArrayList<>();

        // 遍历所有行
        for (Element row : rows) {
            Elements cols = row.select("td");  // 选择行中的所有单元格

            if (cols.size() < 13) {
                // 如果某行的数据不完整，跳过该行
                continue;
            }

            // 提取成绩字段，有些可能是链接
            String score = cols.get(5).select("a").isEmpty() ? cols.get(5).text() : cols.get(5).select("a").text();

            // 解析一行的数据并绑定到 CourseGrade
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