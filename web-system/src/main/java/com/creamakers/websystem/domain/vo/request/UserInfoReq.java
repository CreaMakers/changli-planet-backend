package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoReq implements Serializable {
    @JsonProperty("name")
    String name;

    @JsonProperty("password")
    String password;
}
