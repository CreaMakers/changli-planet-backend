package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.ViolationRecordMapper;
import com.creamakers.websystem.domain.dto.ViolationRecord;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ViolationRecordReq;
import com.creamakers.websystem.domain.vo.response.ViolationRecordResp;
import com.creamakers.websystem.domain.vo.response.ViolationRecordRespCount;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse;
import com.creamakers.websystem.domain.vo.response.ViolationStatsResponse1;
import com.creamakers.websystem.service.ViolationRecordService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.creamakers.websystem.constants.CommonConst.DATA_DELETE_FAILED_NOT_FOUND;
import static com.creamakers.websystem.constants.CommonConst.VIOLATION_RECORD_NOT_SUPPORTED;

@Service
public class ViolationRecordServiceImpl implements ViolationRecordService {

    @Autowired
    private ViolationRecordMapper violationRecordMapper;
    @Override
    public ResultVo findAllViolations(Integer page,Integer pageSize) {
        Page<ViolationRecord> pageParam = new Page<>(page, pageSize);
        Page<ViolationRecord> violationPage = violationRecordMapper.selectPage(pageParam, new QueryWrapper<ViolationRecord>().eq("is_deleted", 0));
        List<ViolationRecord> records = violationPage.getRecords();
        List<ViolationRecordResp> recordRespList = records.stream().map(this::convertToViolationRecordResp).toList();
        ViolationRecordRespCount response = new ViolationRecordRespCount(recordRespList.size(), recordRespList);
        return ResultVo.success(response);
    }

    @Override
    public ResultVo  findAllViolationsById(Long userId, Integer page, Integer pageSize) {
        Page<ViolationRecord> pageParam = new Page<>(page, pageSize);
        Page<ViolationRecord> violationPage = violationRecordMapper.selectPage(pageParam,
                Wrappers.<ViolationRecord>lambdaQuery().eq(ViolationRecord::getUserId, userId));
        List<ViolationRecord> records = violationPage.getRecords();
        List<ViolationRecordResp> recordRespList = records.stream().map(this::convertToViolationRecordResp).toList();
        ViolationRecordRespCount response = new ViolationRecordRespCount(recordRespList.size(), recordRespList);
        return ResultVo.success(response);
    }
    /*
    添加违规记录
     */

    @Override
    public ResultVo<ViolationRecordResp> addViolationRecord(ViolationRecordReq violationRecordRep) {
        ViolationRecord violationRecord = new ViolationRecord();
        BeanUtil.copyProperties(violationRecordRep, violationRecord);
        violationRecordMapper.insert(violationRecord);
        return ResultVo.success(convertToViolationRecordResp(violationRecord));
    }

    /*
    更新用户的违规信息
     */
    @Override
    public ResultVo<ViolationRecordResp> updateViolationRecord(Long violationId, ViolationRecordReq violationRecordRep) {
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ViolationRecord::getId, violationId);
        ViolationRecord violationRecord = violationRecordMapper.selectOne(queryWrapper);
        if (violationRecord == null) {
            return ResultVo.fail(VIOLATION_RECORD_NOT_SUPPORTED);
        }
        BeanUtil.copyProperties(violationRecordRep, violationRecord);
        violationRecordMapper.updateById(violationRecord);
        return ResultVo.success(convertToViolationRecordResp(violationRecord));
    }
    /*
    删除用户的违规记录
     */
    @Override
    public ResultVo<Void> deleteViolationRecord(Long violationId) {
        int i = violationRecordMapper.deleteById(violationId);
        if(i<1) return ResultVo.fail(DATA_DELETE_FAILED_NOT_FOUND);
        return ResultVo.success();
    }
/*
    根据违规类型或时间范围搜索违规记录
 */
    @Override
    public ResultVo<List<ViolationRecordResp>> searchViolationRecord(String violationType, String startTime, String endTime, Integer page, Integer pageSize) {
        Page<ViolationRecord> pageParam = new Page<>(page, pageSize);
        Page<ViolationRecord> violationPage = violationRecordMapper.selectPage(pageParam,
                Wrappers.<ViolationRecord>lambdaQuery()
                        .eq(StringUtils.isNotBlank(violationType), ViolationRecord::getViolationType, violationType)
                        .ge(StringUtils.isNotBlank(startTime), ViolationRecord::getCreateTime, startTime)
                        .le(StringUtils.isNotBlank(endTime), ViolationRecord::getCreateTime, endTime));
        List<ViolationRecord> records = violationPage.getRecords();
        List<ViolationRecordResp> recordRespList = records.stream().map(this::convertToViolationRecordResp).toList();
        return ResultVo.success(recordRespList);
    }
    /*
    获取违规行为的统计数据（个人版 如违规次数、处罚次数）
     */
    @Override
    public ResultVo<ViolationStatsResponse> getViolationStatistics(Long userId) {
       /*
       根据用户名查询违规记录，统计违规次数、处罚次数
        */
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ViolationRecord::getUserId, userId)
                .eq(ViolationRecord::getIsDeleted, 0);
        //return getViolationStatsResponseResultVo(queryWrapper);
        List<ViolationRecord> violationRecords = violationRecordMapper.selectList(queryWrapper);
        ViolationStatsResponse response = new ViolationStatsResponse();
        response.setAllAount(violationRecords.size());
        response.setCount1((int) violationRecords.stream().filter(record -> record.getViolationType().equals(1)).count());
        response.setCount2((int) violationRecords.stream().filter(record -> record.getViolationType().equals(2)).count());
        response.setCounto((int) violationRecords.stream().filter(record -> record.getViolationType().equals(3)).count());
        response.setUnpublishedCount((int) violationRecords.stream().filter(record -> record.getPenaltyStatus().equals(0)).count());
        response.setPublishingCount((int) violationRecords.stream().filter(record -> record.getPenaltyStatus().equals(1)).count());
        response.setPublishedCount((int) violationRecords.stream().filter(record -> record.getPenaltyStatus().equals(2)).count());
        return ResultVo.success(response);
    }
    /*
        获取违规行为的统计数据（所有人）
     */
    @Override
    public ResultVo<List<ViolationStatsResponse1>> getAllViolationStatistics() {
        // 获取所有违规记录,除去删除了的
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ViolationRecord::getIsDeleted, 0);

        // 获取统计数据并按用户分组
        @NotNull List<ViolationStatsResponse1> statsResponses = getViolationStatsResponse1ResultVo(queryWrapper);

        return ResultVo.success(statsResponses);
    }

    private @NotNull List<ViolationStatsResponse1> getViolationStatsResponse1ResultVo(QueryWrapper<ViolationRecord> queryWrapper) {
        // 获取所有违规记录
        List<ViolationRecord> violationRecords = violationRecordMapper.selectList(queryWrapper);

        // 使用 Map 来按 userId 分组并统计每个用户的违规记录
        Map<Integer, ViolationStatsResponse1> userStatsMap = new HashMap<>();

        // 遍历所有违规记录，根据 userId 进行统计
        for (ViolationRecord record : violationRecords) {
            Integer userId = record.getUserId();
            // 获取当前用户的统计对象，如果不存在则创建
            ViolationStatsResponse1 response = userStatsMap.getOrDefault(userId, new ViolationStatsResponse1());
            response.setUserId(userId);
            // 统计违规类型
            if (record.getViolationType() == 1) {
                response.setCount1(response.getCount1() + 1);
            } else if (record.getViolationType() == 2) {
                response.setCount2(response.getCount2() + 1);
            } else if (record.getViolationType() == 3) {
                response.setCounto(response.getCounto() + 1);
            }

            // 统计处罚状态
            if (record.getPenaltyStatus() == 0) {
                response.setUnpublishedCount(response.getUnpublishedCount() + 1);
            } else if (record.getPenaltyStatus() == 1) {
                response.setPublishingCount(response.getPublishingCount() + 1);
            } else if (record.getPenaltyStatus() == 2) {
                response.setPublishedCount(response.getPublishedCount() + 1);
            }
            // 更新 Map 中的用户统计
            userStatsMap.put(userId, response);
        }

        // 返回每个用户的统计结果列表
        return new ArrayList<>(userStatsMap.values());
    }


    private ViolationRecordResp convertToViolationRecordResp(ViolationRecord record) {
        ViolationRecordResp resp = new ViolationRecordResp();
        BeanUtil.copyProperties(record, resp);
        return resp;
    }
}
