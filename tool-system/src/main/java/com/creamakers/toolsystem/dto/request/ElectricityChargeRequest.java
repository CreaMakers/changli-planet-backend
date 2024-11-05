package com.creamakers.toolsystem.dto.request;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "电量请求实体类")
public class ElectricityChargeRequest {
    // 校区地址，如“金盆岭校区”
    private String address;

    // 楼栋名称，如“西苑9栋”
    private String buildId;

    // 宿舍号，如“521”
    private String nod;
}
