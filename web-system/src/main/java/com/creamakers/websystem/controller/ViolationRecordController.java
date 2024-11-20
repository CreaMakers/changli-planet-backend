package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ViolationRecordReq;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse1;
import com.creamakers.websystem.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
违规记录接口
 */
@RestController
@RequestMapping("/web")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping("/violations")
    public ResultVo<List<ViolationRecordResp>> findAllViolations(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize) {
        return violationRecordService.findAllViolations(page, pageSize);
    }

    @GetMapping("/users/{userId}/violations")
    public ResultVo<List<ViolationRecordResp>> findAllViolationsById(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            ) {

        return violationRecordService.findAllViolationsById(userId, page, pageSize);
    }
    /*
    记录违规行为接口
     */
    @PostMapping("/violations")
    public ResultVo<ViolationRecordResp> addViolationRecord(@RequestBody ViolationRecordReq violationRecordRep) {
        return violationRecordService.addViolationRecord(violationRecordRep);
    }
    @PutMapping("/violations/{violationId}")
    public ResultVo<ViolationRecordResp> updateViolationRecord(@PathVariable(value = "violationId") Long violationId, @RequestBody ViolationRecordReq violationRecordRep) {
        return violationRecordService.updateViolationRecord(violationId, violationRecordRep);
    }
    @DeleteMapping("/violations/{violationId}")
    public ResultVo<Void> deleteViolationRecord(@PathVariable(value = "violationId") Long violationId) {
        return violationRecordService.deleteViolationRecord(violationId);
    }
    /*
     * 根据违规类型或者时间搜索违规记录
     *
     */
    @GetMapping("/violation/search")
    public ResultVo<List<ViolationRecordResp>> searchViolationRecord(
            @RequestParam(value = "violation_type", required = false) String violationType,
            @RequestParam(value = "start_time", required = false) String startTime,
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        return violationRecordService.searchViolationRecord(violationType, startTime, endTime, page, pageSize);
    }
    /*
    获取违规行为的统计数据（个人版 如违规次数、处罚次数）
     */
    @GetMapping("users/{userId}/violations/statistics")
    public ResultVo<ViolationStatsResponse> getViolationStatistics(@PathVariable(value = "userId") Long userId) {
        return violationRecordService.getViolationStatistics(userId);
    }
    /*
    获取违规行为的统计数据（所有人 如违规次数、处罚次数）
     */
    @GetMapping("/violations/statistics")
    public ResultVo<List<ViolationStatsResponse1>> getAllViolationStatistics() {
        return violationRecordService.getAllViolationStatistics();
    }
    /*
    对某个违规行为进行处罚
     */
    @PutMapping("/violations/{violationId}/punishments")
    public ResultVo<ViolationRecordResp> penaltyViolationRecord(@PathVariable(value = "violationId") Long violationId, @RequestBody ViolationRecordReq violationRecordRep) {
        return violationRecordService.updateViolationRecord(violationId, violationRecordRep);
    }


}
