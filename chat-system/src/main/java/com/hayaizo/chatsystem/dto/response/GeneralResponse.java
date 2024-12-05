package com.hayaizo.chatsystem.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "通用响应实体类，用于封装成功或失败的响应")
public class GeneralResponse<T> {

    @ApiModelProperty(value = "响应状态码", example = "400")
    private String code;

    @ApiModelProperty(value = "提示信息", example = "Operation failed")
    private String msg;

    @ApiModelProperty(value = "响应数据，成功时为具体数据，失败时为错误详情")
    private T data;
}