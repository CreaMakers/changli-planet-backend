package com.creamakers.websystem.service;


import com.creamakers.websystem.domain.dto.ReportUser;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ReportUserReq;

import java.util.List;

public interface ReportService {
    ResultVo<List<ReportUser>> getAllReports();
    ResultVo<ReportUser> updateReportById(String reportId, ReportUserReq userPunishmentReq);

}
