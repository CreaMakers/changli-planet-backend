package com.creamakers.toolsystem.dto.response;

import com.creamakers.toolsystem.consts.HttpCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data // Lombok注解，自动生成 getter、setter、toString、hashCode、equals 等方法
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
@Builder // 生成构造者模式
@Accessors(chain = true) // 支持链式调用
@ApiModel(description = "通用响应实体类，用于封装成功或失败的响应") // Swagger注解，用于生成API文档
public class GeneralResponse<T> {

    @ApiModelProperty(value = "响应状态码", example = "400")
    private String code;

    @ApiModelProperty(value = "提示信息", example = "Operation failed")
    private String msg;

    @ApiModelProperty(value = "响应数据，成功时为具体数据，失败时为错误详情")
    private T data;

    // 成功响应（无数据）
    public static <T> GeneralResponse<T> success() {
        return GeneralResponse.<T>builder()
                .code(HttpCode.OK)
                .msg("success")
                .build();
    }

    // 成功响应（带数据）
    public static <T> GeneralResponse<T> success(T data) {
        return GeneralResponse.<T>builder()
                .code(HttpCode.OK)
                .msg("success")
                .data(data)
                .build();
    }

    // 成功响应（自定义消息）
    public static <T> GeneralResponse<T> success(String msg, T data) {
        return GeneralResponse.<T>builder()
                .code(HttpCode.OK)
                .msg(msg)
                .data(data)
                .build();
    }

    // 失败响应（简单版）
    public static <T> GeneralResponse<T> fail(String msg) {
        return GeneralResponse.<T>builder()
                .code(HttpCode.INTERNAL_SERVER_ERROR)
                .msg(msg)
                .build();
    }

    // 失败响应（自定义状态码和消息）
    public static <T> GeneralResponse<T> fail(String code, String msg) {
        return GeneralResponse.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    // 失败响应（带数据）
    public static <T> GeneralResponse<T> fail(String msg, T data) {
        return GeneralResponse.<T>builder()
                .code(HttpCode.INTERNAL_SERVER_ERROR)
                .msg(msg)
                .data(data)
                .build();
    }
}
