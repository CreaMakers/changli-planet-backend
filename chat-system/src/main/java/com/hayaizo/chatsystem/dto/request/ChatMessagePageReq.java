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
public class ChatMessagePageReq extends CursorPageBaseReq {
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}

