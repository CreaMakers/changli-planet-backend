package com.creamakers.websystem.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupMessageResp {
    private Integer messageId;
    private Integer groupId;
    private Integer senderId;
    private Integer receiverId;
    private String messageContent;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private Integer isDeleted;
    private String description;
}
