package com.creamakers.fresh.system.controller;


import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.ReportCommentRequest;
import com.creamakers.fresh.system.domain.vo.request.ReportFreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.ReportCommentResp;
import com.creamakers.fresh.system.domain.vo.response.ReportNewsResp;
import com.creamakers.fresh.system.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/app/fresh_news")
public class ReportController {

    @Autowired
    private ReportService reportService;
    /*
    举报新鲜事
     */
    @PostMapping("/report/add")
    public ResultVo<Void> reportFreshNews(@Valid @RequestBody ReportFreshNewsRequest request) {
        return reportService.reportFreshNews(request);
    }
    /*
    举报评论
     */
    @PostMapping("/comments/report/add")
    public ResultVo<Void> reportComment(@Valid @RequestBody ReportCommentRequest request) {
        return reportService.reportComment(request);
    }
    /**
     * 查询举报新鲜事的处理状态
     * @param reportId 举报ID
     * @return 结果
     */
    @GetMapping("/report/{report_id}/status")
    public ResultVo<ReportNewsResp> getFreshNewsReportStatus(@PathVariable("report_id") Long reportId) {
        return reportService.getFreshNewsReportStatus(reportId);
    }

    /**
     * 查询举报评论的处理状态
     * @param commentId 评论ID
     * @return 结果
     */
    @GetMapping("/comments/report/{comment_id}/status")
    public ResultVo<ReportCommentResp> getCommentReportStatus(@PathVariable("comment_id") Long commentId) {
        return reportService.getCommentReportStatus(commentId);
    }
}
