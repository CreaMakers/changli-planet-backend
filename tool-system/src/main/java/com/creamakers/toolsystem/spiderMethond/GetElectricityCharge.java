package com.creamakers.toolsystem.spiderMethond;


import com.creamakers.toolsystem.entity.ElectricityCharge;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;

public class GetElectricityCharge {
    HashMap<String, String> buildingMap;

    {
        buildingMap = new HashMap<>();
        // 填充数据
        buildingMap.put("金盆岭校区", "0030000000002502");
        buildingMap.put("西苑2栋", "9");
        buildingMap.put("东苑11栋", "178");
        buildingMap.put("西苑5栋", "33");
        buildingMap.put("东苑14栋", "132");
        buildingMap.put("东苑6栋", "131");
        buildingMap.put("南苑7栋", "97");
        buildingMap.put("东苑9栋", "162");
        buildingMap.put("西苑11栋", "75");
        buildingMap.put("西苑6栋", "41");
        buildingMap.put("东苑4栋", "171");
        buildingMap.put("西苑8栋", "57");
        buildingMap.put("东苑15栋", "133");
        buildingMap.put("西苑9栋", "65");
        buildingMap.put("南苑5栋", "96");
        buildingMap.put("西苑10栋", "74");
        buildingMap.put("东苑12栋", "179");
        buildingMap.put("南苑4栋", "95");
        buildingMap.put("东苑5栋", "130");
        buildingMap.put("西苑3栋", "17");
        buildingMap.put("西苑4栋", "25");
        buildingMap.put("外教楼", "180");
        buildingMap.put("南苑3栋", "94");
        buildingMap.put("西苑7栋", "49");
        buildingMap.put("西苑1栋", "1");
        buildingMap.put("南苑8栋", "98");
        buildingMap.put("云塘校区", "0030000000002501");
        buildingMap.put("16栋A区", "471");
        buildingMap.put("16栋B区", "472");
        buildingMap.put("17栋", "451");
        buildingMap.put("弘毅轩1栋A区", "141");
        buildingMap.put("弘毅轩1栋B区", "148");
        buildingMap.put("弘毅轩2栋A区1-6楼", "197");
        buildingMap.put("弘毅轩2栋B区", "201");
        buildingMap.put("弘毅轩2栋C区", "205");
        buildingMap.put("弘毅轩2栋D区", "206");
        buildingMap.put("弘毅轩3栋A区", "155");
        buildingMap.put("弘毅轩3栋B区", "183");
        buildingMap.put("弘毅轩4栋A区", "162");
        buildingMap.put("弘毅轩4栋B区", "169");
        buildingMap.put("留学生公寓", "450");
        buildingMap.put("敏行轩1栋A区", "176");
        buildingMap.put("敏行轩1栋B区", "184");
        buildingMap.put("敏行轩2栋A区", "513");
        buildingMap.put("敏行轩2栋B区", "520");
        buildingMap.put("敏行轩3栋A区", "527");
        buildingMap.put("敏行轩3栋B区", "528");
        buildingMap.put("敏行轩4栋A区", "529");
        buildingMap.put("敏行轩4栋B区", "530");
        buildingMap.put("行健轩1栋A区", "85");
        buildingMap.put("行健轩1栋B区", "92");
        buildingMap.put("行健轩2栋A区", "99");
        buildingMap.put("行健轩2栋B区", "106");
        buildingMap.put("行健轩3栋A区", "113");
        buildingMap.put("行健轩3栋B区", "120");
        buildingMap.put("行健轩4栋A区", "127");
        buildingMap.put("行健轩4栋B区", "134");
        buildingMap.put("行健轩5栋A区", "57");
        buildingMap.put("行健轩5栋B区", "64");
        buildingMap.put("行健轩6栋A区", "71");
        buildingMap.put("行健轩6栋B区", "78");
        buildingMap.put("至诚轩1栋A区", "1");
        buildingMap.put("至诚轩1栋B区", "8");
        buildingMap.put("至诚轩2栋A区", "15");
        buildingMap.put("至诚轩2栋B区", "22");
        buildingMap.put("至诚轩3栋A区", "29");
        buildingMap.put("至诚轩3栋B区", "36");
        buildingMap.put("至诚轩4栋B区", "50");
        buildingMap.put("至诚轩4栋A区", "43");
    }
    String url = "http://yktwd.csust.edu.cn:8988/web/Common/Tsm.html";
    public ElectricityCharge getCharge(String address, String buildId, String Nod) {
        OkHttpClient client = new OkHttpClient();
        // 表单数据
        String jsondata = String.format("{\"query_elec_roominfo\": { \"aid\":\"%s\", \"account\": \"293924\",\"room\": { \"roomid\": \"%s\", \"room\":\" \" }, \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"%s\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"%s\", \"building\": \"\" } }}",
                buildingMap.get(address), Nod, address, buildingMap.get(buildId));
        RequestBody formBody = new FormBody.Builder()
                .add("jsondata", jsondata)
                .add("funname", "synjones.onecard.query.elec.roominfo")
                .add("json", "true")
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Cookie", "JSESSIONID=6666666")
                .build();
        // 发送请求并获取响应
        String responseBody = null;
        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String msg = null;
        if("系统异常!".equals(responseBody)) {
            msg = responseBody;
        }
        else {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();
            JsonObject queryElecRoomInfo = jsonObject.getAsJsonObject("query_elec_roominfo");
            msg = queryElecRoomInfo.get("errmsg").getAsString();
        }
        return new ElectricityCharge(msg);
    }
}
