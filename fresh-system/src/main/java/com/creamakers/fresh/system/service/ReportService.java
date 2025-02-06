package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.ReportCommentRequest;
import com.creamakers.fresh.system.domain.vo.request.ReportFreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.ReportCommentResp;
import com.creamakers.fresh.system.domain.vo.response.ReportNewsResp;

import javax.validation.Valid;

public interface ReportService {
    ResultVo<Void> reportFreshNews(@Valid ReportFreshNewsRequest request);

    ResultVo<Void> reportComment(@Valid ReportCommentRequest request);

    ResultVo<ReportNewsResp> getFreshNewsReportStatus(Long reportId);

    ResultVo<ReportCommentResp> getCommentReportStatus(Long commentId);
}
