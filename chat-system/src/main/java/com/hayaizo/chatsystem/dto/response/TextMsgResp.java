package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgResp {
    @ApiModelProperty("消息内容")
    private String content;
    @ApiModelProperty("消息链接映射")
    private Map<String, UrlInfo> urlContentMap;
    @ApiModelProperty("艾特的uid")
    private List<Long> atUidList;
}
