package com.hayaizo.chatsystem.dto.response;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息返回体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "message", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("用户id")
    private Integer uid;
    @ApiModelProperty("消息详情")
    private Message message;

    @Data
    public static class Message {
        @ApiModelProperty("消息id")
        private Integer id;
        @ApiModelProperty("房间id")
        private Integer roomId;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型")
        private Object body;
    }
}
