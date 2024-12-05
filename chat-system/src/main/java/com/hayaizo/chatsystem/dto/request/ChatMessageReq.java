package com.hayaizo.chatsystem.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {

    @NotNull
    @ApiModelProperty("用户id")
    private Integer uid;

    @NotNull
    @ApiModelProperty("房间id")
    private Integer roomId;

    @NotNull
    @ApiModelProperty("消息类型")
    private Integer msgType;

    @NotNull
    @ApiModelProperty("消息内容，类型不同传值不同")
    private Object body;
}