package com.creamakers.websystem.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPunishmentReq {

    private int reportedUserId;

    private int punishmentType;

    private String processDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime handleTime;
    @JsonFormat(pattern = "HH:mm:ss")

    private int punishmentDuration;
}
