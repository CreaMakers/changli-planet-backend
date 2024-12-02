package com.hayaizo.chatsystem.dto.response;

import lombok.Data;

/**
 * ws的基本返回信息体
 */
@Data
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息
     */
    private Integer type;
    private T data;
}
