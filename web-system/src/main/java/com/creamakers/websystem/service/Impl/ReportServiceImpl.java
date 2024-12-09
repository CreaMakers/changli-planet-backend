package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.dao.ReportUserMapper;
import com.creamakers.websystem.dao.UserMapper;
import com.creamakers.websystem.dao.ViolationRecordMapper;
import com.creamakers.websystem.domain.dto.ReportUser;
import com.creamakers.websystem.domain.dto.User;
import com.creamakers.websystem.domain.dto.ViolationRecord;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ReportUserReq;
import com.creamakers.websystem.domain.vo.request.UserPunishmentReq;
import com.creamakers.websystem.service.ReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
  @Autowired
  private ReportUserMapper reportUserMapper;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private ViolationRecordMapper violationRecordMapper;
    @Override
    public ResultVo<List<ReportUser>> getAllReports() {
      List<ReportUser> list;
        list = reportUserMapper.selectList(new QueryWrapper<ReportUser>().eq("is_deleted", 0));
        return  ResultVo.success(list);


    }
    @Override
    public ResultVo<ReportUser> updateReportById(String reportId, ReportUserReq reportUserReq) {
        // 通过reportId拿到举报信息
        ReportUser reportUser = reportUserMapper.selectOne(new QueryWrapper<ReportUser>().eq("report_id", reportId));
        if (reportUser == null) {
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, "查询举报ID无效，请重新输入");
        }

        // 通过reported_user_id拿到user信息
        User user = userMapper.selectById(reportUser.getReportedUserId());
        if (user == null) {
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.BAD_USERINFO_QUERY);
        }

        // 处理不同类型处罚插入到违规表中
        switch (reportUserReq.getPenaltyType()) {
            case 0 -> // 不处罚
                    updateReportUser(reportUser, reportUserReq.getProcessDescription());
            case 1 -> { // 警告
                ViolationRecord warningRecord = createViolationRecord(reportUserReq, reportUser, null, null);
                violationRecordMapper.insert(warningRecord);
                updateReportUser(reportUser, reportUserReq.getProcessDescription());
            }
            case 2 -> { // 封禁
                user.setIsBanned(1);
                userMapper.updateById(user);
                ViolationRecord banRecord = createViolationRecord(reportUserReq, reportUser, reportUserReq.getPunishmentDuration(), null);
                violationRecordMapper.insert(banRecord);
                updateReportUser(reportUser, reportUserReq.getProcessDescription());
            }
            case 3 -> { // 禁言
                ViolationRecord muteRecord = createViolationRecord(reportUserReq, reportUser, null, reportUserReq.getPunishmentDuration());
                violationRecordMapper.insert(muteRecord);
                updateReportUser(reportUser, reportUserReq.getProcessDescription());
            }
            default -> {
                return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, "未知的处罚类型");
            }
        }

        return ResultVo.success(reportUser);
    }

    private void updateReportUser(ReportUser reportUser, String processDescription) {
        reportUser.setStatus(1);
        reportUser.setProcessDescription(processDescription);
        reportUserMapper.updateById(reportUser);
    }

    private ViolationRecord createViolationRecord(ReportUserReq reportUserReq, ReportUser reportUser, Integer banDurationDays, Integer muteDurationDays) {
        ViolationRecord violationRecord = new ViolationRecord();
        BeanUtil.copyProperties(reportUserReq, violationRecord);
        violationRecord.setUserId(reportUser.getReportedUserId());
        violationRecord.setPenaltyStatus(1);
        violationRecord.setPenaltyTime(LocalDateTime.now());
        if (banDurationDays != null) {
            violationRecord.setBanDuration(banDurationDays * 24 * 60); // 转换为分钟
        }
        if (muteDurationDays != null) {
            violationRecord.setMuteDuration(muteDurationDays * 24 * 60); // 转换为分钟
        }
        return violationRecord;
    }









  }



