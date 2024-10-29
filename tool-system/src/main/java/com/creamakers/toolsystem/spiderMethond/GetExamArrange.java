package com.creamakers.toolsystem.spiderMethond;


import com.creamakers.toolsystem.entity.ExamArrange;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 获取考试安排
public class GetExamArrange {

    private final String url = "http://xk.csust.edu.cn/jsxsd/xsks/xsksap_list";
    private final String cookies;

    // 构造函数，接受Cookie作为String类型
    public GetExamArrange(String cookies) {

        // 将传入的 cookies 与 getJwCode()[2] 拼接
        String combinedCookies = cookies;

        // 使用正则表达式提取 "JSESSIONID" 和 "SERVERID_jsxsd" 相关的部分
        StringBuilder retainedCookies = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(JSESSIONID=[^;]*|SERVERID_jsxsd=[^;]*)").matcher(combinedCookies);
        while (matcher.find()) {
            // 添加每个匹配到的部分，并加上分号分隔
            retainedCookies.append(matcher.group()).append("; ");
        }

        this.cookies = retainedCookies.toString().trim();
    }

    // 获取考试安排数据
    public List<ExamArrange> getData(String term, String examType) throws IOException {

        // 使用Jsoup连接请求，并设置Cookie作为字符串
        Connection con = Jsoup.connect(url)
                .followRedirects(false)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookies)   // 设置传入的Cookie
                .data("xqlbmc", examType)  // 考试类别
                .data("xnxqid", term)  // 学年学期ID
                .data("xqlb", "3");  // 固定数据，代表"期末"

        // 发送请求并获取响应
        Connection.Response res = con.execute();

        // 解析HTML
        Document doc = Jsoup.parse(res.body());

        // 检查是否存在数据
        Element emptyDataElement = doc.selectFirst("td[colspan=10]");  // 查找显示“未查询到数据”的 <td> 元素
        if (emptyDataElement != null && emptyDataElement.text().contains("未查询到数据")) {
            // 如果未找到数据，返回空列表或抛出异常
            System.out.println("未查询到考试安排数据");

            return new LinkedList<ExamArrange>();
        }

        List<Element> examInfo = doc.select("#dataList > tbody > tr > td");  // 选择表格中的每个单元格

        // 存储考试安排
        List<ExamArrange> examList = new ArrayList<>();

        // 解析并存储每一行的考试安排信息
        for (int i = 0; i < examInfo.size(); ) {
            examList.add(new ExamArrange(
                    Integer.parseInt(examInfo.get(i).text()),  // ID
                    examInfo.get(i + 1).text(),  // 课程名称
                    examInfo.get(i + 2).text(),  // 考试日期
                    examInfo.get(i + 3).text(),  // 开始时间
                    examInfo.get(i + 4).text(),  // 结束时间
                    examInfo.get(i + 5).text(),  // 考试地点
                    examInfo.get(i + 6).text(),  // 座位号
                    examInfo.get(i + 7).text()   // 监考老师
            ));
            i += 12;  // 每次增加12个单元格，表示一行考试数据
        }


        return examList;
    }
}
