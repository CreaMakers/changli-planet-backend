package com.creamakers.toolsystem.spiderMethond;


import com.creamakers.toolsystem.entity.CourseGrade;
import com.creamakers.toolsystem.entity.PscjInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GetCourseGrade {

    public PscjInfo getScoreDetail(String pscjUrl) throws IOException, InterruptedException {
        int maxRetries = 7;  // 最大重试次数
        int currentTry = 0;

        while (currentTry < maxRetries) {
            Document document = Jsoup.connect(pscjUrl)
                    .header("Cookie", cookie)
                    .get();

            // 检查是否是登录页面
            if (isLoginPage(document)) {
                currentTry++;
                if (currentTry >= maxRetries) {
                    return null;
                }
                // 可以在这里添加重新登录的逻辑
                Thread.sleep(20);
                continue;
            }

            return parseScoreDetailHtml(document);
        }

        return null;
    }

    private PscjInfo parseScoreDetailHtml(Document document) {
        try {
            HashMap<String, Integer> map = new HashMap<>();
            Elements elements = document.select("#dataList");
            Elements trs = elements.select("tr");
            Element nameTr = null;
            try {
                nameTr = trs.get(0);
            } catch (Exception e) {
                log.error(document.toString());
            }
            Elements ths = nameTr.select("th");
            int index = 0;
            for (Element th : ths) {
                String name = th.ownText();
                switch (name) {
                    case "平时成绩":
                        map.put("pscj", index);
                        break;
                    case "平时成绩比例":
                        map.put("pscjBL", index);
                        break;
                    case "期末成绩":
                        map.put("qmcj", index);
                        break;
                    case "期末成绩比例":
                        map.put("qmcjBL", index);
                        break;
                    case "总成绩":
                        map.put("totalScore", index);
                        break;
                    case "期中成绩":
                        map.put("qzcj", index);
                        break;
                    case "期中成绩比例":
                        map.put("qzcjBL", index);
                        break;
                    case "上机成绩":
                        map.put("sjcj", index);
                    case "上机成绩比例":
                        map.put("sjcjBL", index);
                }
                index++;
            }

            Element pscjTr = trs.get(1);
            Elements tds = pscjTr.select("td");
            PscjInfo pscjInfo = new PscjInfo();

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                if ("pscj".equals(key)) {
                    pscjInfo.setPscj(tds.get(value).ownText());
                } else if ("pscjBL".equals(key)) {
                    pscjInfo.setPscjBL(tds.get(value).ownText());
                } else if ("qmcj".equals(key)) {
                    pscjInfo.setQmcj(tds.get(value).ownText());
                } else if ("qmcjBL".equals(key)) {
                    pscjInfo.setQmcjBL(tds.get(value).ownText());
                } else if ("totalScore".equals(key)) {
                    pscjInfo.setScore(tds.get(value).ownText());
                } else if ("qzcj".equals(key)) {
                    pscjInfo.setQzcj(tds.get(value).ownText());
                } else if ("qzcjBL".equals(key)) {
                    pscjInfo.setQzcjBL(tds.get(value).ownText());
                } else if ("sjcj".equals(key)) {
                    pscjInfo.setSjcj(tds.get(value).ownText());
                } else if ("sjcjBL".equals(key)) {
                    pscjInfo.setSjcjBL(tds.get(value).ownText());
                }
            }
            return pscjInfo;
        } catch (Exception e) {
            throw new RuntimeException("解析平时成绩失败", e);
        }
    }

    private boolean isLoginPage(Document document) {
        // 检查HTML内容
        String htmlContent = document.html();

        // 检查是否是登录页面或错误页面
        return document.select("form[action='/jsxsd/xk/LoginToXk']").size() > 0
                || document.select("#userAccount").size() > 0
                || htmlContent.contains("alert('数据有误！')")
                || document.select("script:contains(alert)").size() > 0
                || document.select("body").isEmpty()
                || document.select("head > script:contains(window.close)").size() > 0;
    }

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