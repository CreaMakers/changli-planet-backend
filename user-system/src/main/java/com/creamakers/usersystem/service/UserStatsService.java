package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.UserStats;

public interface UserStatsService extends IService<UserStats> {

    GeneralResponse getStats(String accessToken);

    GeneralResponse getStatsById(String userId);

    GeneralResponse setStudentNumber(String studentNumber,String accessToken);

}
