package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ViolationRecordReq;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse1;

import java.util.List;

public interface ViolationRecordService {
    ResultVo<List<ViolationRecordResp>> findAllViolations(Integer page, Integer pageSize);

    ResultVo<List<ViolationRecordResp>> findAllViolationsById(Long userId, Integer page, Integer pageSize);

    ResultVo<ViolationRecordResp> updateViolationRecord(Long violationId, ViolationRecordReq violationRecordRep);

    ResultVo<Void> deleteViolationRecord(Long violationId);

    ResultVo<List<ViolationRecordResp>> searchViolationRecord(String violationType, String startTime, String endTime, Integer page, Integer pageSize);

    ResultVo<ViolationStatsResponse> getViolationStatistics(Long userId);

    ResultVo<ViolationRecordResp> addViolationRecord(ViolationRecordReq violationRecordRep);

    ResultVo<List<ViolationStatsResponse1>> getAllViolationStatistics();
}
