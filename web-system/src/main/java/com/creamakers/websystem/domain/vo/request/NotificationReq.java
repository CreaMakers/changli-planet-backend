package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationReq {

    @JsonProperty("senderId")
    private Long senderId;  // 发送者ID

    @JsonProperty("content")
    private String content; // 通知内容

    @JsonProperty("description")
    private String description;  // 通知描述
}
