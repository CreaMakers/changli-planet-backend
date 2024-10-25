package com.creamakers.usersystem.dto.response;

import lombok.Data;

@Data
public class RefreshAuthResp {
    /**
     * accessToken
     */
    private String accessToken;

    public RefreshAuthResp(String accessToken) {
        this.accessToken = accessToken;
    }
}
