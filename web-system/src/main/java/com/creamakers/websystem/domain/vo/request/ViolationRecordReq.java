package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationRecordReq {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("violation_type")
    private Integer violationType;

    @JsonProperty("penalty_type")
    private Integer penaltyType;

    @JsonProperty("penalty_status")
    private Integer penaltyStatus;

    @JsonProperty("violation_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime violationTime;

    @JsonProperty("penalty_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime penaltyTime;

    @JsonProperty("mute_duration")
    private Integer muteDuration;

    @JsonProperty("ban_duration")
    private Integer banDuration;

    @JsonProperty("penalty_reason")
    private String penaltyReason;

    @JsonProperty("is_deleted")
    private Integer isDeleted;

    @JsonProperty("description")
    private String description;
}