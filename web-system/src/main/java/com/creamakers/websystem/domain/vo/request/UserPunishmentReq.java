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

    private Integer UserId;

    private  Integer penaltyType;


   private Integer mutedTime;

}
