package com.creamakers.fresh.system.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.fresh.system.dao.FreshNewsLikesMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsLikes;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;
import com.creamakers.fresh.system.service.FreshNewsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreshNewsServiceImpl implements FreshNewsService {
    @Autowired
    private FreshNewsMapper freshNewsMapper;
    @Autowired
    private FreshNewsLikesMapper freshNewsLikesMapper;
    @Override
    public ResultVo<Void> createFreshNews(FreshNewsRequest freshNewsRequest) {
        FreshNews freshNews = new FreshNews();
        freshNews.setUserId(freshNewsRequest.getUserId())
                .setTitle(freshNewsRequest.getTitle())
                .setContent(freshNewsRequest.getContent())
                .setImages(freshNewsRequest.getImages())
                .setTags(freshNewsRequest.getTags())
                .setAllowComments(freshNewsRequest.getAllowComments());
        int rows = freshNewsMapper.insert(freshNews);
        if (rows > 0) {
            return ResultVo.success();
        } else {
            return ResultVo.fail("创建新鲜事失败");
        }
    }

    @Override
    public ResultVo<FreshNewsDetailResp> getFreshNewsById(Long freshNewsId) {
        FreshNews freshNewsDetail = freshNewsMapper.selectById(freshNewsId);
        if (freshNewsDetail != null) {
            FreshNewsDetailResp freshNewsDetailResp = new FreshNewsDetailResp();

            // 使用 BeanUtils 复制属性
            BeanUtils.copyProperties(freshNewsDetail, freshNewsDetailResp);

            // 处理 images 字段，将逗号分隔的字符串转换为数组
            if (freshNewsDetail.getImages() != null && !freshNewsDetail.getImages().isEmpty()) {
                String[] imageArray = freshNewsDetail.getImages().split(",");
                freshNewsDetailResp.setImages(Arrays.asList(imageArray));  // 转换为 List
            } else {
                freshNewsDetailResp.setImages(new ArrayList<>());  // 空字符串或 null 时返回空数组
            }

            // 处理 tags 字段，将逗号分隔的字符串转换为数组
            if (freshNewsDetail.getTags() != null && !freshNewsDetail.getTags().isEmpty()) {
                String[] tagArray = freshNewsDetail.getTags().split(",");
                freshNewsDetailResp.setTags(Arrays.asList(tagArray));  // 转换为 List
            } else {
                freshNewsDetailResp.setTags(new ArrayList<>());  // 空字符串或 null 时返回空数组
            }

            return ResultVo.success(freshNewsDetailResp);
        } else {
            return ResultVo.fail("新鲜事不存在");
        }
    }


    @Override
    public ResultVo<List<FreshNewsResp>> getAllFreshNews(Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNews> pageParam = new Page<>(page, pageSize);

        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam, new QueryWrapper<FreshNews>().eq("is_deleted", 0).orderByDesc("create_time"));

        List<FreshNews> records = pageResult.getRecords();

        List<FreshNewsResp> freshNewsRespList = records.stream()
                    .map(freshNews -> {
                        FreshNewsResp resp = new FreshNewsResp();
                        BeanUtils.copyProperties(freshNews, resp);
                        return resp;
                    })
                    .collect(Collectors.toList());
        return ResultVo.success(freshNewsRespList);
    }

    @Override
    public ResultVo<List<FreshNewsResp>> getAllByLikes(Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNews> pageParam = new Page<>(page, pageSize);

        // 执行分页查询，过滤已删除的记录，并按点赞数降序排列
        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
                new QueryWrapper<FreshNews>().eq("is_deleted", 0).orderByDesc("liked", "create_time"));

        // 获取查询结果
        List<FreshNews> records = pageResult.getRecords();

        // 转换查询结果为响应对象
        List<FreshNewsResp> freshNewsRespList = records.stream()
                .map(freshNews -> {
                    FreshNewsResp resp = new FreshNewsResp();
                    BeanUtils.copyProperties(freshNews, resp);
                    return resp;
                })
                .collect(Collectors.toList());

        // 返回分页结果
        return ResultVo.success(freshNewsRespList);
    }


    @Override
    public ResultVo<List<FreshNewsResp>> getByTag(String tag, Integer page, Integer pageSize) {
        Page<FreshNews> pageParam = new Page<>(page, pageSize);
        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
                new QueryWrapper<FreshNews>()
                        .eq("is_deleted", 0) // 只查询未删除的记录
                        .like("tags", tag)   // 查询标签包含给定tag的记录
                        .orderByDesc("create_time")
        );
        List<FreshNews> records = pageResult.getRecords();
        List<FreshNewsResp> freshNewsRespList = records.stream()
                .map(freshNews -> {
                    FreshNewsResp resp = new FreshNewsResp();
                    BeanUtils.copyProperties(freshNews, resp);
                    return resp;
                })
                .collect(Collectors.toList());
        return ResultVo.success(freshNewsRespList);
    }
}
