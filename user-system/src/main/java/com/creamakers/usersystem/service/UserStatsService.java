package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface UserStatsService extends IService<UserStats> {

    ResponseEntity<GeneralResponse> getStats(String accessToken);

    ResponseEntity<GeneralResponse> getStatsById(String userId);

    ResponseEntity<GeneralResponse> setStudentNumber(String studentNumber,String accessToken);

    Boolean initializeUserStats(User user);

    Boolean updateUserStats(UserStats userStats);

    ResponseEntity<GeneralResponse> createResponseEntity(HttpStatus status, String code, String msg, Object data);
}
