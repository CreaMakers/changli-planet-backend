package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 消息返回体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {

    @ApiModelProperty("发送者信息")
    private Integer fromUser;
    @ApiModelProperty("消息详情")
    private Message message;

    @Data
    public static class Message {
        @ApiModelProperty("消息id")
        private Integer id;
        @ApiModelProperty("房间id")
        private Integer groupID;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容")
        private Object body;
    }
}
