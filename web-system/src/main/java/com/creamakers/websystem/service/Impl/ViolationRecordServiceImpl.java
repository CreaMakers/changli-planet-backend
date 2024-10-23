package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.ViolationRecordMapper;
import com.creamakers.websystem.domain.dto.UserProfile;
import com.creamakers.websystem.domain.dto.ViolationRecord;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.UserProfileResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

@Service
public class ViolationRecordServiceImpl implements ViolationRecordService {

    @Autowired
    private ViolationRecordMapper violationRecordMapper;
    @Override
    public ResultVo<List<ViolationRecordResp>> findAllViolations(Integer page, Integer pageSize) {
        Page<ViolationRecord> pageParam = new Page<>(page, pageSize);
        Page<ViolationRecord> violationPage = violationRecordMapper.selectPage(pageParam, null);
        List<ViolationRecord> records = violationPage.getRecords();
        List<ViolationRecordResp> recordRespList = records.stream().map(this::convertToViolationRecordResp).toList();
        return ResultVo.success(recordRespList);
    }

    @Override
    public ResultVo<List<ViolationRecordResp>> findAllViolationsById(Long userId, Integer page, Integer pageSize) {
        Page<ViolationRecord> pageParam = new Page<>(page, pageSize);
        Page<ViolationRecord> violationPage = violationRecordMapper.selectPage(pageParam,
                Wrappers.<ViolationRecord>lambdaQuery().eq(ViolationRecord::getUserId, userId));
        List<ViolationRecord> records = violationPage.getRecords();
        List<ViolationRecordResp> recordRespList = records.stream().map(this::convertToViolationRecordResp).toList();
        return ResultVo.success(recordRespList);
    }

    private ViolationRecordResp convertToViolationRecordResp(ViolationRecord record) {
        ViolationRecordResp resp = new ViolationRecordResp();
        BeanUtil.copyProperties(record, resp);
        return resp;
    }
}
