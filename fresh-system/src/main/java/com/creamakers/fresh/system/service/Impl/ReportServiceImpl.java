package com.creamakers.fresh.system.service.Impl;

import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.dao.ReportCommentMapper;
import com.creamakers.fresh.system.dao.ReportFreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.ReportComment;
import com.creamakers.fresh.system.domain.dto.ReportFreshNews;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.ReportCommentRequest;
import com.creamakers.fresh.system.domain.vo.request.ReportFreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.ReportCommentResp;
import com.creamakers.fresh.system.domain.vo.response.ReportNewsResp;
import com.creamakers.fresh.system.service.ReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import static com.creamakers.fresh.system.constants.CommonConst.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportCommentMapper reportCommentMapper;

    @Autowired
    private ReportFreshNewsMapper reportFreshNewsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;  // 注入 RedisTemplate

    /**
     * 举报新鲜事
     *
     * @param request 举报请求
     * @return 结果
     */
    @Override
    public ResultVo<Void> reportFreshNews(ReportFreshNewsRequest request) {
        Long newsId = request.getFreshNewsId();
        Long userId = request.getReporterId();

        // 检查该用户是否已经举报过该新鲜事（使用 Redis）
        String redisKey = RedisKeyConstant.REPORT_NEWS + ":" + newsId;

        Boolean isReported = redisTemplate.opsForSet().isMember(redisKey, userId);

        if (isReported != null && isReported) {
            return ResultVo.fail(ALREADY_REPORTED_FRESH_NEWS_MESSAGE);  // 用户已经举报过
        }

        // 将举报记录添加到 Redis，表示该用户已举报该新闻
        redisTemplate.opsForSet().add(redisKey, userId);
        // 设置过期时间（例如：24小时）
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);

        ReportFreshNews reportFreshNews = new ReportFreshNews();
        BeanUtils.copyProperties(request, reportFreshNews);

        // 插入举报记录到数据库
        int rows = reportFreshNewsMapper.insert(reportFreshNews);

        if (rows > 0) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(REPORT_FRESH_NEWS_FAILED_MESSAGE);
        }
    }

    /**
     * 举报评论
     *
     * @param request 举报请求
     * @return 结果
     */
    @Override
    public ResultVo<Void> reportComment(ReportCommentRequest request) {
        Long commentId = request.getCommentId();
        Long userId = request.getReporterId();

        // 检查该用户是否已经举报过该评论（使用 Redis）
        String redisKey = RedisKeyConstant.REPORT_COMMENT + ":" + commentId;

        Boolean isReported = redisTemplate.opsForSet().isMember(redisKey, userId);

        if (isReported != null && isReported) {
            return ResultVo.fail(ALREADY_REPORTED_COMMENT_MESSAGE);  // 用户已经举报过
        }

        // 将举报记录添加到 Redis，表示该用户已举报该评论
        redisTemplate.opsForSet().add(redisKey, userId);
        // 设置过期时间（例如：24小时）
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);

        // 创建举报记录
        ReportComment reportComment = new ReportComment();
        BeanUtils.copyProperties(request,reportComment);

        // 插入举报记录到数据库
        int rows = reportCommentMapper.insert(reportComment);

        if (rows > 0) {
            return ResultVo.success();
        } else {
            return ResultVo.fail(REPORT_COMMENT_FAILED_MESSAGE);
        }
    }

    /**
     * 查询新鲜事举报状态
     *
     * @param reportId 举报ID
     * @return 举报状态
     */
    @Override
    public ResultVo<ReportNewsResp> getFreshNewsReportStatus(Long reportId) {
        // 查询新闻的举报状态
        ReportFreshNews reportFreshNews = reportFreshNewsMapper.selectById(reportId);
        ReportNewsResp reportNewsResp = new ReportNewsResp();
        BeanUtils.copyProperties(reportFreshNews,reportNewsResp);
        if (reportFreshNews != null) {
            return ResultVo.success(reportNewsResp);  // 返回举报信息
        }
        return ResultVo.fail(FRESH_NEWS_REPORT_RECORD_NOT_FOUND_MESSAGE);
    }

    /**
     * 查询评论举报状态
     *
     * @param commentId 评论ID
     * @return 举报状态
     */
    @Override
    public ResultVo<ReportCommentResp> getCommentReportStatus(Long commentId) {
        // 查询评论的举报状态
        ReportComment reportComment = reportCommentMapper.selectById(commentId);
        ReportCommentResp reportCommentResp = new ReportCommentResp();
        BeanUtils.copyProperties(reportComment,reportCommentResp);
        if (reportCommentResp != null) {
            return ResultVo.success(reportCommentResp);  // 返回举报信息
        }
        return ResultVo.fail(COMMENT_REPORT_RECORD_NOT_FOUND_MESSAGE);
    }
}
