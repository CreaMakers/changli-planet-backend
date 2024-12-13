package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.dto.ReportPost;
import com.creamakers.websystem.domain.dto.ReportUser;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ReportUserReq;
import com.creamakers.websystem.domain.vo.request.UserPunishmentReq;
import com.creamakers.websystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/reports")
public class ReportController {
  @Autowired
  private ReportService reportService;

  /**
   * 获取所有用户对用户的举报
   * @return
   */
  @GetMapping("/users")
  public ResultVo<List<ReportUser>> getAllReports(){
    return reportService.getAllReports();




  }

  @PutMapping("/posts/{reportId}")
  public ResultVo<ReportUser>  updateReportById(@PathVariable String reportId, @RequestBody ReportUserReq reportUserReq){
    return reportService.updateReportById(reportId,reportUserReq);
  }
/**
 * 强制禁言或者封号
 */
@PostMapping("/user/penalties")
public ResultVo forcedBan(@RequestBody UserPunishmentReq userPunishmentReq){
  return reportService.forceBanById(userPunishmentReq);

}


}
