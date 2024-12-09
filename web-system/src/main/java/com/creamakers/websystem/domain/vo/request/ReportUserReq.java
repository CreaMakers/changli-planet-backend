package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportUserReq {
//    private Long reportedUserId;
    private Integer penaltyType;
    private Integer violationType;
    private String penaltyReason;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime violationTime;
    private Integer punishmentDuration;
    private String description;
    private String processDescription;
}
