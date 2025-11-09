package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PenaltyReq;
import com.creamakers.websystem.domain.vo.response.FreshNewsResp;
import com.creamakers.websystem.domain.vo.response.ReportCommentResp;
import com.creamakers.websystem.domain.vo.response.ReportNewsResp;

import java.util.List;

public interface FreshNewsService {
    ResultVo<List<ReportNewsResp>> getAllReports(Integer page, Integer pageSize);

    ResultVo<ReportNewsResp> handleReportNewsPenalty(PenaltyReq penaltyReq);

    ResultVo<List<ReportCommentResp>> getAllCommentReports(Integer page, Integer pageSize);

    ResultVo<ReportCommentResp> handleReportCommentPenalty(PenaltyReq penaltyReq);

    ResultVo<FreshNewsResp> deleteFreshNews(Long freshNewsId);
}
