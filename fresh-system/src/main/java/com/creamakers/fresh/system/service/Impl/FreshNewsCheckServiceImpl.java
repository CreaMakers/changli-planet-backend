package com.creamakers.fresh.system.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.dao.FreshNewsCheckMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsCheck;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCheckResp;
import com.creamakers.fresh.system.service.FreshNewsCheckService;
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
                .set("update_time",LocalDateTime.now());
        int FreshNewsUpdated = freshNewsMapper.update(FreshNewsUpdateWrapper);
        if(FreshNewsUpdated <= 0){
            // 新鲜事图片路径更新失败
            return ResultVo.fail(CommonConst.FRESH_NEWS_IMAGE_UPDATE_FAILED_MESSAGE);
        }
        log.info("新鲜事审核通过，审核记录ID：{}，新鲜事ID：{}", freshNewsCheckId,freshNewsCheck.getFreshNewsId());
        return ResultVo.success();
    }

    // 新增新鲜事审核记录
    @Override
    public boolean addFreshNewsCheck(FreshNews freshNews, String finalUrls)throws IOException {
        FreshNewsCheck freshNewsCheck = new FreshNewsCheck()
                .setFreshNewsId(freshNews.getFreshNewsId())
                .setTitle(freshNews.getTitle())
                .setContent(freshNews.getContent())
                .setImageUrl(finalUrls)
                .setCheckStatus(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setIsDeleted(0)
                .setCheckTime(null);
        int insert = freshNewsCheckMapper.insert(freshNewsCheck);
        if(insert <= 0){
            // 新增新鲜事审核记录失败，抛出异常
            throw new IOException(CommonConst.NEWS_CHECK_FAILED_MESSAGE);
        }
        log.info("新增新鲜事审核记录，审核记录ID：{}，新鲜事ID：{}", freshNewsCheck.getFreshNewsCheckId(),freshNewsCheck.getFreshNewsId());
        return true;
    }
}
