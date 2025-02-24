package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PenaltyReq;

import com.creamakers.websystem.domain.vo.response.ReportCommentResp;
import com.creamakers.websystem.domain.vo.response.ReportNewsResp;
import com.creamakers.websystem.service.FreshNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/fresh_news")
public class FreshNewsController {

    @Autowired
    private FreshNewsService freshNewsService;

    // 获取所有新鲜事举报
    @GetMapping("/reports/all")
    public ResultVo<List<ReportNewsResp>> getAllReports(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllReports(page, pageSize);
    }

    // 处理新鲜事举报并执行处罚
    @PostMapping("/reports/penalties")
    public ResultVo<ReportNewsResp> handleReportNewsPenalty(@RequestBody PenaltyReq penaltyReq) {
        return freshNewsService.handleReportNewsPenalty(penaltyReq);
    }

    // 获取所有评论举报
    @GetMapping("/comments/reports/all")
    public ResultVo<List<ReportCommentResp>> getAllCommentReports(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllCommentReports(page, pageSize);
    }

    // 处理评论举报并执行处罚
    @PostMapping("/comments/reports/penalties")
    public ResultVo<ReportCommentResp> handleReportCommentPenalty(@RequestBody PenaltyReq penaltyReq) {
        return freshNewsService.handleReportCommentPenalty(penaltyReq);
    }
}
