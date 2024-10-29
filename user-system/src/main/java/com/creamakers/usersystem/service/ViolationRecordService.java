package com.creamakers.usersystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.po.ViolationRecord;

public interface ViolationRecordService extends IService<ViolationRecord> {

    GeneralResponse getViolations(String accessToken);

    GeneralResponse getViolationsByUserId(Integer userId);
}
