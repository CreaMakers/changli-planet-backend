package com.hayaizo.chatsystem.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgReq {

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("回复的消息id,如果没有别传就好")
    private Long replyMsgId;

    @ApiModelProperty("艾特的uid")
    private List<Long> atUidList;
}
