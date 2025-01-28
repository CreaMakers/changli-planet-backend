package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupAnnounceReq {
    @JsonProperty("title")
    private String title;         // 群规更新通知标题

    @JsonProperty("content")
    private String content;       // 群规更新通知内容

    @JsonProperty("isPinned")
    private boolean isPinned;     // 是否置顶

    @JsonProperty("description")
    private String description;   // 群规更新通知的描述
}
