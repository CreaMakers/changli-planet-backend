package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.ViolationRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ViolationRecordService extends IService<ViolationRecord> {

    ResponseEntity<GeneralResponse>  getViolations(String accessToken);

    ResponseEntity<GeneralResponse> getViolationsByUserId(Integer userId);

    ResponseEntity<GeneralResponse> createResponseEntity(HttpStatus status, String code, String msg, Object data);
}
