package com.creamakers.websystem.domain.vo.request;

import com.creamakers.websystem.domain.dto.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAllInfoReq {

    private UserReq userReq;

    private UserProfileReq userProfileReq;

    private UserStatsReq userStatsReq;
}
