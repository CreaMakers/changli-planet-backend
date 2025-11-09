package com.creamakers.websystem.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.*;
import com.creamakers.websystem.domain.dto.*;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.PenaltyReq;
import com.creamakers.websystem.domain.vo.response.FreshNewsResp;
import com.creamakers.websystem.domain.vo.response.NotificationResp;
import com.creamakers.websystem.domain.vo.response.ReportCommentResp;
import com.creamakers.websystem.domain.vo.response.ReportNewsResp;
import com.creamakers.websystem.service.FreshNewsCommentService;
import com.creamakers.websystem.service.FreshNewsService;
import com.creamakers.websystem.utils.HUAWEIOBSUtil;
import com.creamakers.websystem.utils.WebSocketService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.creamakers.websystem.constants.CommonConst.FRESH_NEWS_NOT_FOUND_MESSAGE;

@Service
public class FreshNewsServiceImpl implements FreshNewsService {

    @Autowired
    private FreshNewsMapper freshNewsMapper;

    @Autowired
    private ReportFreshNewsMapper reportFreshNewsMapper;

    @Autowired
    private ReportCommentMapper reportCommentMapper;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private FreshNewsCommentService freshNewsCommentService;

    // 获取所有新鲜事举报
    @Override
    public ResultVo<List<ReportNewsResp>> getAllReports(Integer page, Integer pageSize) {
        Page<ReportFreshNews> pageParam = new Page<>(page, pageSize);
        Page<ReportFreshNews> resultPage = reportFreshNewsMapper.selectPage(pageParam, new QueryWrapper<ReportFreshNews>().eq("is_deleted", 0));
        List<ReportFreshNews> records = resultPage.getRecords();
        List<ReportNewsResp> reportNewsRespList = records.stream()
                .map(this::convertToReportNewsResp)
                .collect(Collectors.toList());
        return ResultVo.success(reportNewsRespList);
    }

    // 处理新鲜事举报并执行处罚
    @Override
    @Transactional
    public ResultVo<ReportNewsResp> handleReportNewsPenalty(PenaltyReq penaltyReq) {
        ReportFreshNews reportFreshNews = reportFreshNewsMapper.selectById(penaltyReq.getReportId());
        reportFreshNews.setPenaltyType(penaltyReq.getPenaltyType())
                .setProcessDescription(penaltyReq.getProcessDescription())
                .setStatus(1)
                .setProcessTime(LocalDateTime.now());
        if(penaltyReq.getPenaltyType().equals(0)){
            reportFreshNewsMapper.updateById(reportFreshNews);
        }
        else if(penaltyReq.getPenaltyType().equals(1)){
            FreshNews freshNews = freshNewsMapper.selectById(reportFreshNews.getFreshNewsId());
            freshNews.setAllowComments(0);
            freshNewsMapper.updateById(freshNews);
            reportFreshNewsMapper.updateById(reportFreshNews);
        }
        else if(penaltyReq.getPenaltyType().equals(2)){
            FreshNews freshNews = freshNewsMapper.selectById(reportFreshNews.getFreshNewsId());
            freshNews.setIsDeleted(1);
            freshNewsMapper.updateById(freshNews);
            reportFreshNewsMapper.updateById(reportFreshNews);
        }else{
            ResultVo.fail("处罚类型错误");
        }
        // 创建通知对象，通知举报者或被举报者
        Notification notification = new Notification();
        notification.setNotificationType(1)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSenderId(1L)
                .setReceiverId(reportFreshNews.getReporterId())
                .setSendTime(LocalDateTime.now())
                .setCreateTime(LocalDateTime.now())
                .setDescription("您的举报已被处理");
        notification.setContent(penaltyReq.getProcessDescription());

        notificationMapper.insert(notification);

        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification,notificationResp);

        webSocketService.sendMessageToUser(reportFreshNews.getReporterId(),notificationResp);

        ReportNewsResp reportNewsResp = new ReportNewsResp();
        BeanUtils.copyProperties(reportFreshNews,reportNewsResp);
        return ResultVo.success(reportNewsResp);
    }

    // 获取所有评论举报
    @Override
    public ResultVo<List<ReportCommentResp>> getAllCommentReports(Integer page, Integer pageSize) {
        Page<ReportComment> pageParam = new Page<>(page, pageSize);
        Page<ReportComment> resultPage = reportCommentMapper.selectPage(pageParam, new QueryWrapper<ReportComment>().eq("is_deleted", 0));
        List<ReportComment> records = resultPage.getRecords();
        List<ReportCommentResp> reportCommentRespList = records.stream()
                .map(this::convertToReportCommentResp)
                .collect(Collectors.toList());
        return ResultVo.success(reportCommentRespList);
    }

    // 处理评论举报并执行处罚
    @Override
    @Transactional
    public ResultVo<ReportCommentResp> handleReportCommentPenalty(PenaltyReq penaltyReq) {
        ReportComment reportComment = reportCommentMapper.selectById(penaltyReq.getReportId());
        reportComment.setPenaltyType(penaltyReq.getPenaltyType())
                .setProcessDescription(penaltyReq.getProcessDescription())
                .setStatus(1)
                .setProcessTime(LocalDateTime.now());
        if(penaltyReq.getPenaltyType().equals(0)){
            reportCommentMapper.updateById(reportComment);
        }
        else if(penaltyReq.getPenaltyType().equals(1)){
            // 删除评论
            freshNewsCommentService.deleteComment(reportComment.getCommentId(),reportComment.getIsParent());
            reportCommentMapper.updateById(reportComment);
        } else{
            ResultVo.fail("处罚类型错误");
        }
        // 创建通知对象，通知举报者或被举报者
        Notification notification = new Notification();
        notification.setNotificationType(1)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSenderId(1L)
                .setReceiverId(reportComment.getReporterId())
                .setSendTime(LocalDateTime.now())
                .setCreateTime(LocalDateTime.now())
                .setDescription("您的举报已被处理")
                .setContent(penaltyReq.getProcessDescription());

        notificationMapper.insert(notification);

        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification,notificationResp);

        webSocketService.sendMessageToUser(reportComment.getReporterId(),notificationResp);
        ReportCommentResp reportCommentResp = new ReportCommentResp();
        BeanUtils.copyProperties(reportComment,reportCommentResp);
        sendMessageToReporter(reportComment);
        return ResultVo.success(reportCommentResp);
    }

    @Override
    @Transactional
    public ResultVo<FreshNewsResp> deleteFreshNews(Long freshNewsId) {

        if (freshNewsMapper.selectById(freshNewsId) == null) {
            return ResultVo.fail(FRESH_NEWS_NOT_FOUND_MESSAGE);
        }

        FreshNews freshNews = freshNewsMapper.selectById(freshNewsId);
        FreshNewsResp resp = new FreshNewsResp();
        BeanUtils.copyProperties(freshNews, resp);

        // 删除新鲜事
        freshNewsMapper.updateById(freshNews.setIsDeleted(1));
        // 删除评论
        freshNewsCommentService.deleteCommentByFreshNewsId(freshNewsId);
        return ResultVo.success(resp);
    }

    private ReportNewsResp convertToReportNewsResp(ReportFreshNews reportFreshNews) {
        ReportNewsResp reportNewsResp = new ReportNewsResp();
        BeanUtils.copyProperties(reportFreshNews,reportNewsResp);
        return reportNewsResp;
    }

    private ReportCommentResp convertToReportCommentResp(ReportComment comment) {
        ReportCommentResp reportCommentResp = new ReportCommentResp();
        BeanUtils.copyProperties(comment,reportCommentResp);
        return reportCommentResp;
    }

    private void sendMessageToReporter(ReportComment reportComment) {
        // 获取举报用户的信息
        return ;
    }
}
