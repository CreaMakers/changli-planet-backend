package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/web")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping("/violations")
    public ResultVo<List<ViolationRecordResp>> findAllViolations(@RequestParam(value = "page") Integer page, @RequestParam(value = "page_size") Integer pageSize) {
        return violationRecordService.findAllViolations(page, pageSize);
    }

    @GetMapping("/users/{userId}/violations")
    public ResultVo<List<ViolationRecordResp>> findAllViolationsById(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "page_size") Integer pageSize
            ) {

        return violationRecordService.findAllViolationsById(userId, page, pageSize);
    }


}
