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


public class GetExamArrange {

    private final String url = "http://xk.csust.edu.cn/jsxsd/xsks/xsksap_list";
    private final String cookies;


    public GetExamArrange(String cookies) {


        String combinedCookies = cookies;


        StringBuilder retainedCookies = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(JSESSIONID=[^;]*|SERVERID_jsxsd=[^;]*)").matcher(combinedCookies);
        while (matcher.find()) {
            retainedCookies.append(matcher.group()).append("; ");
        }

        this.cookies = retainedCookies.toString().trim();
    }


    public List<ExamArrange> getData(String term, String examType) throws IOException {

//String xqlb = examType.equals("期末") ? "3" : "2";
        String xqlb = null;
        if(examType.equals("期中")) {
            xqlb = "2";
        } else if(examType.equals("期末")) {
            xqlb = "3";
        } else {
            xqlb = "";
        }
        Connection con = Jsoup.connect(url)
                .followRedirects(false)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", cookies)
                .data("xqlbmc", examType)
                .data("xnxqid", term)
                .data("xqlb", xqlb);


        Connection.Response res = con.execute();


        Document doc = Jsoup.parse(res.body());

        Element emptyDataElement = doc.selectFirst("td[colspan=10]");
        if (emptyDataElement != null && emptyDataElement.text().contains("未查询到数据")) {
            System.out.println("未查询到考试安排数据");

            return new LinkedList<ExamArrange>();
        }

        List<Element> examInfo = doc.select("#dataList > tbody > tr > td");


        List<ExamArrange> examList = new ArrayList<>();


        for (int i = 0; i < examInfo.size(); ) {
            examList.add(new ExamArrange(
                    // ID
                    Integer.parseInt(examInfo.get(i).text()),
                    // 课程名称
                    examInfo.get(i + 1).text(),
                    // 考试日期
                    examInfo.get(i + 2).text(),
                    // 开始时间
                    examInfo.get(i + 3).text(),
                    // 结束时间
                    examInfo.get(i + 4).text(),
                    // 考试地点
                    examInfo.get(i + 5).text(),
                    // 座位号
                    examInfo.get(i + 6).text(),
                    // 监考老师
                    examInfo.get(i + 7).text()
            ));
            // 每次增加12个单元格，表示一行考试数据
            i += 12;
        }


        return examList;
    }
}
