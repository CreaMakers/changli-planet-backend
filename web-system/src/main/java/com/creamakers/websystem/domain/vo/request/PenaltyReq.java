package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyReq {

    @JsonProperty("penaltyType")
    private Integer penaltyType;  // 惩罚类型

    @JsonProperty("reportId")
    private Long reportId;  // 举报ID

    @JsonProperty("processDescription")
    private String processDescription;  // 处理描述
}
