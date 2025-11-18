package com.creamakers.toolsystem.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel(description = "主题皮肤响应体")
public class TopicSkinResponse {
    @ApiModelProperty(value = "主键id",example = "1")
    private Integer id;
    @ApiModelProperty(value = "皮肤名称",example = "默认皮肤")
    private String name;
    @ApiModelProperty(value = "资源完整性校验值",example = "123456")
    private String hashMd5;
}
