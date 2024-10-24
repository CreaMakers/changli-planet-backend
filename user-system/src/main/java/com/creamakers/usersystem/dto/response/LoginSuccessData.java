package com.creamakers.usersystem.dto.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data // Lombok注解，自动生成getter、setter、toString、hashCode、equals等方法
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
@Builder // 生成构造者模式
@Accessors(chain = true) // 支持链式调用
@ApiModel(description = "用户实体类") // Swagger注解，用于生成API文档
public class LoginSuccessData {

    @ApiModelProperty(value = "访问令牌")
    private String access_token;

    @ApiModelProperty(value = "刷新令牌")
    private String refresh_token;

    @ApiModelProperty(value = "令牌有效期（秒）")
    private String expires_in;
}
