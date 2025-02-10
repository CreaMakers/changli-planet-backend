package com.creamakers.toolsystem.entity;

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
@ApiModel(description = "平时成绩详情类")
public class PscjInfo {
    /**
     * 平时成绩
     */
    private String pscj;
    /**
     * 平时成绩比例
     */
    private String pscjBL;

    /**
     * 期末成绩
     */
    private String qmcj;

    /**
     * 期末成绩比例
     */
    private String qmcjBL;
    /**
     * 期中成绩
     */
    private String qzcj;
    /**
     * 期中成绩比例
     */
    private String qzcjBL;

    /**
     * 总成绩
     */
    private String score;
    /*
     * 上机成绩
     */
    private  String sjcj;
    /*
     上机成绩比例
     */
    private String sjcjBL;
}
