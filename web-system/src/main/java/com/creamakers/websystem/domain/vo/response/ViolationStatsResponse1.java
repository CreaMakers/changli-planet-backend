package com.creamakers.websystem.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
所有人版
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationStatsResponse1 {
    private int userId;
    private int count1;
    private int count2;
    private int counto;
    private int allAount;
    private int unpublishedCount;
    private int publishingCount;
    private int publishedCount;
}
