package com.creamakers.usersystem.dto.request;


import io.swagger.annotations.ApiModel;
import jakarta.validation.constraints.NotBlank;
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
@ApiModel(description = "通用成功响应实体类") // Swagger注解，用于生成API文档
public class LoginByEmailRequest {
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
