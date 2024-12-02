package com.hayaizo.chatsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 记录和前端连接的一些映射信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {
    /**
     * 前端如果登录了，记录uid
     */
    private Integer uid;
}
