package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.dao.FreshNewsCheckMapper;
import com.creamakers.websystem.dao.FreshNewsMapper;
import com.creamakers.websystem.domain.dto.FreshNews;
import com.creamakers.websystem.domain.dto.FreshNewsCheck;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.FreshNewsCheckResp;
import com.creamakers.websystem.service.FreshNewsCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FreshNewsCheckServiceImpl implements FreshNewsCheckService {
    @Autowired
    private FreshNewsCheckMapper freshNewsCheckMapper;
    @Autowired
    private FreshNewsMapper freshNewsMapper;

    @Override
    public ResultVo<List<FreshNewsCheckResp>> findAllFreshNewsCheck(Integer page, Integer pageSize) {
        // 分页查询
        Page p = new Page<>(page,pageSize);
        Wrapper<FreshNewsCheck> wrapper = new QueryWrapper<FreshNewsCheck>().eq("is_deleted", 0);
        Page selectedPage = freshNewsCheckMapper.selectPage(p, wrapper);
        List<FreshNewsCheckResp> collect = selectedPage.getRecords()
                .stream().map(FreshNewsCheck -> BeanUtil.copyProperties(FreshNewsCheck, FreshNewsCheckResp.class)).toList();
        log.info("查询所有新鲜事审核记录，页码：{}，每页数量：{}",page,pageSize);
        return ResultVo.success(collect);
    }

    @Override
    public ResultVo<List<FreshNewsCheckResp>> findFreshNewsCheckByQuery(Long freshNewsCheckId, Long freshNewsId, Integer checkStatus, Integer page, Integer pageSize) {
        Page p = new Page<>(page,pageSize);
        Wrapper<FreshNewsCheck> wrapper = new QueryWrapper<FreshNewsCheck>()
                .eq("is_deleted", 0)
                .eq(freshNewsCheckId != null,"fresh_news_check_id",freshNewsCheckId)
                .eq(freshNewsId != null,"fresh_news_id",freshNewsId)
                .eq(checkStatus != null,"check_status",checkStatus);
        Page selectedPage = freshNewsCheckMapper.selectPage(p, wrapper);
        List<FreshNewsCheckResp> collect = selectedPage.getRecords()
                .stream().map(FreshNewsCheck -> BeanUtil.copyProperties(FreshNewsCheck, FreshNewsCheckResp.class)).toList();
        log.info("根据条件查询新鲜事审核记录，页码：{}，每页数量：{}，审核记录ID：{}，新鲜事ID：{}，审核状态：{}",
                page,pageSize,freshNewsCheckId,freshNewsId,checkStatus);
        return ResultVo.success(collect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo<Void> checkFreshNews(Long freshNewsCheckId, Integer checkStatus) {
        FreshNewsCheck freshNewsCheck = freshNewsCheckMapper.selectById(freshNewsCheckId);
        if(freshNewsCheck == null){
            // 新鲜事审核记录不存在
            return ResultVo.fail(CommonConst.NEWS_CHECK_NOT_FIND_MASSAGE);
        }

        // 更新新鲜事审核记录
        freshNewsCheck
                .setCheckStatus(checkStatus)
                .setCheckTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        UpdateWrapper<FreshNewsCheck> freshNewsCheckUpdateWrapper = new UpdateWrapper<>();
        freshNewsCheckUpdateWrapper.eq("is_deleted", 0)
                .eq("fresh_news_check_id", freshNewsCheckId);
        int freshNewsCheckUpdate = freshNewsCheckMapper.update(freshNewsCheck, freshNewsCheckUpdateWrapper);

        if(freshNewsCheckUpdate <= 0){
            // 新鲜事审核提交失败
            return ResultVo.fail(CommonConst.NEWS_CHECK_FAILED_MESSAGE);
        }

        if(checkStatus == 2){
            // 审核拒绝，更新新鲜事图片路径为“审核拒绝”
            freshNewsCheck.setImageUrl("审核拒绝");
        }
        // 更新新鲜事图片路径
        UpdateWrapper<FreshNews> FreshNewsUpdateWrapper = new UpdateWrapper<>();
        FreshNewsUpdateWrapper
                .eq("fresh_news_id",freshNewsCheck.getFreshNewsId())
                .set("images",freshNewsCheck.getImageUrl())
                .set("is_active",checkStatus == 2? 0 : 1)
                .set("update_time",LocalDateTime.now());
        int FreshNewsUpdated = freshNewsMapper.update(FreshNewsUpdateWrapper);
        if(FreshNewsUpdated <= 0){
            // 新鲜事图片路径更新失败
            return ResultVo.fail(CommonConst.FRESH_NEWS_IMAGE_UPDATE_FAILED_MESSAGE);
        }
        log.info("新鲜事审核通过，审核记录ID：{}，新鲜事ID：{}", freshNewsCheckId,freshNewsCheck.getFreshNewsId());
        return ResultVo.success();
    }

    @Override
    public ResultVo<Void> deleteFreshNewsCheck(Long freshNewsCheckId) {
        UpdateWrapper<FreshNewsCheck> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("fresh_news_check_id",freshNewsCheckId)
                .set("is_deleted",1)
                .set("update_time",LocalDateTime.now());
        int freshNewsCheckDeleted = freshNewsCheckMapper.update(null, updateWrapper);
        if(freshNewsCheckDeleted <= 0){
            // 新鲜事审核记录删除失败
            return ResultVo.fail(CommonConst.NEWS_CHECK_DELETE_FAILED_MESSAGE);
        }
        log.info("删除新鲜事审核记录，审核记录ID：{}", freshNewsCheckId);
        return ResultVo.success();
    }
}
