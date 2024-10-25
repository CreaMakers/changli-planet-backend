package com.creamakers.websystem.domain.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
@Builder // 生成构造者模式
@Accessors(chain = true) // 支持链式调用
@ApiModel(description = "登陆成功返回token类") // Swagger注解，用于生成API文档

public class LoginTokenResp {

    @ApiModelProperty(value = "访问令牌")
    private String access_token;

    @ApiModelProperty(value = "令牌有效期（秒）")
    private String expires_in;

}
