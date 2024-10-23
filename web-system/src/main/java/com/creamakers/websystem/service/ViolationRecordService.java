package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ViolationRecordService {
    ResultVo<List<ViolationRecordResp>> findAllViolations(Integer page, Integer pageSize);

    ResultVo<List<ViolationRecordResp>> findAllViolationsById(Long userId, Integer page, Integer pageSize);
}
