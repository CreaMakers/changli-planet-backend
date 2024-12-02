package com.hayaizo.chatsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccessResp {
    private Integer uid;
    private String avatar;
    private String token;
    private String name;
    //用户权限 0普通用户 1超管
    private Byte power;
}
