package com.creamakers.websystem.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAllInfoResp {
    private UserResp userResp;

    private UserProfileResp userProfileResp;

    private UserStatsResp userStatsResp;
}
