package com.creamakers.toolsystem.spiderMethond;

import okhttp3.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GetCookies {

    private int loopIndex = 0;
    //从教务系统获取code的url
    private static final String GET_CODE_URL = "http://xk.csust.edu.cn/Logon.do?method=logon&flag=sess";
    //教务系统登录的url
    private static final String LOGIN_URL = "http://xk.csust.edu.cn/Logon.do?method=logon";
    //教务系统学生主页的url
    private static final String XS_MAIN_JSP_URL = "http://xk.csust.edu.cn/jsxsd/framework/xsMain_new.jsp?t1=1";

    //JSESSIONID匹配的正则表达式
    private static final Pattern JW_JSESSIONID_Patten = Pattern.compile("^JSESSIONID=.*");
    private static final Pattern NUMBER_STRING = Pattern.compile("[0-9]*");
    private static final SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat weekDaySdf = new SimpleDateFormat("EEEE");

    private static final String versionRegex = ".+v\\d\\.\\d\\.\\d\\.apk1?|.+v\\d\\.\\d\\.\\d\\.ipa1?";



    public String[] getJwCode() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request jwCodeRequest = new Request.Builder().url(GET_CODE_URL).build();
        Response response = okHttpClient.newCall(jwCodeRequest).execute();

        // 初始化结果数组：第一个元素为 JSESSIONID，第二个为页面数据（dataStr），第三个为 SERVERID_jxsd
        String[] result = new String[3];

        // 创建一个 List 来存储所有的键值对
        List<String> headerValues = new ArrayList<>();

        // 获取所有的响应头
        Headers headers = response.headers();

        // 将所有的响应头名称和值添加到 headerValues 列表中
        for (int i = 0; i < headers.size(); i++) {
            headerValues.add(headers.name(i));  // 添加键
            headerValues.add(headers.value(i));  // 添加值
        }

        // 获取 JSESSIONID 的值，假设它在 headerValues 中的索引 1 处
        String fullCookie = headerValues.get(1);  // 获取 JSESSIONID 的完整字符串
        if (fullCookie.startsWith("JSESSIONID=")) {
            result[0] = fullCookie.split("=")[1].split(";")[0];
            result[2] = headerValues.get(19);// 按照分号分隔，并只保留第一个部分
        }

        // 获取响应体内容（dataStr）
        if (response.body() != null) {
            result[1] = response.body().string(); // 获取页面数据
            response.body().close();
        }

        return result;
    }


    public static String encodePsd(String username, String password, String dataStr) {
        String[] splitCode = dataStr.split("#");
        String scode = splitCode[0];
        String sxh = splitCode[1];
        String code = username + "%%%" + password;
        String encode = "";
        for (int i = 0; i < code.length(); i++) {
            if (i < 20) {
                int theIndex = Integer.parseInt(sxh.substring(i, i + 1));
                encode = encode + code.charAt(i) + scode.substring(0, theIndex);
                scode = scode.substring(theIndex);
            } else {
                encode = encode + code.substring(i);
                i = code.length();
            }
        }
        return encode;
    }

    public String getHeaderFromJW(String stuNum, String password) {

        Response response = null;
        Response updateCookieResponse = null;
        try {


            String[] jwCode = getJwCode();
            //2010AV81T974906Y80071f0Uyf1d21T5F%1%ir%8dlC63h34eg2n4g155123!
            String encoded = encodePsd(stuNum, password, jwCode[1]);
            OkHttpClient okHttpClient = new OkHttpClient.Builder().followRedirects(false).build();
            FormBody formBody = new FormBody.Builder()
//                    .add("userAccount", stuNum)
//                    .add("userPassword", password)
                    .add("encoded", encoded).build();  //加密之后的密码 2S0g012198A20481QX05i10D0Q331172157X5%d%g80%V5C6h56fe9nlg00F123!
            //Request{method=POST, url=http://xk.csust.edu.cn/Logon.do?method=logon, headers=[Cookie:JSESSIONID=D45529F57ADB0287E43350D531DD3EB7, Host:xk.csust.edu.cn, Origin:http://xk.csust.edu.cn, Referer:http://xk.csust.edu.cn/]}
            Request jwLoginRequest = new Request.Builder()
                    .header("Cookie", "JSESSIONID=" + jwCode[0] + ";" + jwCode[2])
                    .header("Host", "xk.csust.edu.cn")
                    .header("Origin", "http://xk.csust.edu.cn")
                    .header("Referer", "http://xk.csust.edu.cn/")
                    .url(LOGIN_URL) //http://xk.csust.edu.cn/Logon.do?method=logon
                    .post(formBody)
                    .build();
            response = okHttpClient.newCall(jwLoginRequest).execute();
            //Response{protocol=http/1.1, code=302, message=, url=http://xk.csust.edu.cn/Logon.do?method=logon}
            //updateCookieUrl： http://xk.csust.edu.cn/jsxsd/xk/LoginToXk?method=jwxt&ticqzket=c910ec98ba5756eeb9f660d4dc2d080f43c1c5abded67a5633d4897d77b9c6ffbbb8594cbcfcf26f371d4308b65a6647af52aa83ecbca08e2e6bed301946493a130570b925f97f396a5de375ca35438eba0f83ba2d745c6adbe6947d891b3bcb505b21504dc53d5eda9115e985e20f4c


//            Headers headers = response.headers();
//            String updateCookieUrl = new String();
//            // 创建一个 List 来存储所有的键值对
//            List<String> headerValues = new ArrayList<>();
//            // 获取所有的响应头
//            // 将所有的响应头名称和值添加到 headerValues 列表中
//            for (int i = 0; i < headers.size(); i++) {
//                headerValues.add(headers.name(i));  // 添加键
//                headerValues.add(headers.value(i));  // 添加值
//            }
//
//            updateCookieUrl = headerValues.get(11);




            String updateCookieUrl = response.header("Location");




            if (updateCookieUrl == null) {
                return null;
            }

            OkHttpClient updateCookieClient = new OkHttpClient.Builder().followRedirects(false).build();
            Request updateCookieRequest = new Request.Builder()
                    .header("Cookie", "JSESSIONID=" + jwCode[0] + ";" + jwCode[2]) //3671960E207E0379E35A75699C898405
                    .header("Referer", "http://xk.csust.edu.cn/")
                    .url(updateCookieUrl)
                    .build();
            updateCookieResponse = updateCookieClient.newCall(updateCookieRequest).execute();
            //JSESSIONID=83F3294B2F3361D1D91F0981ECDE5F49; Path=/jsxsd; HttpOnly


            // 创建一个 List 来存储所有的键值对
            List<String> headerValues = new ArrayList<>();

            // 获取所有的响应头
            Headers headers = updateCookieResponse.headers();

            // 将所有的响应头名称和值添加到 headerValues 列表中
            for (int i = 0; i < headers.size(); i++) {
                headerValues.add(headers.name(i));  // 添加键
                headerValues.add(headers.value(i));  // 添加值
            }

            String cookie = headerValues.get(3) + ";" + jwCode[2];


            return cookie;
        } catch (Exception e) {
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
            if (updateCookieResponse != null) {
                updateCookieResponse.close();
            }
        }
    }
}
