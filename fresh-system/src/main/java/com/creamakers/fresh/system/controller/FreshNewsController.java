package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;

import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;
import com.creamakers.fresh.system.service.FreshNewsService;

import com.creamakers.fresh.system.utils.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/app/fresh_news")
public class FreshNewsController {

    @Autowired
    private FreshNewsService freshNewsService;

    /**
     * 创建新鲜事
     */
    @PostMapping
    public ResultVo<FreshNewsDetailResp> createFreshNews(@RequestParam("images")List<MultipartFile> images,
                                               @RequestParam("fresh_news") String s) throws IOException {
        FreshNewsRequest freshNewsRequest = JsonParser.StoJ(s);
        return freshNewsService.createFreshNews(images,freshNewsRequest);
    }

    /**
     * 根据ID查看新鲜事详情
     * @param freshNewsId 新鲜事ID
     * @return 新鲜事详情
     */
    @GetMapping("/{freshNewsId}")
    public ResultVo<FreshNewsDetailResp> getFreshNewsById(@PathVariable("freshNewsId") Long freshNewsId) {
        return freshNewsService.getFreshNewsById(freshNewsId);
    }

    /**
     * 获取所有新鲜事，并按更新时间排序
     * @param page 页码
     * @param pageSize 每页大小
     * @return 新鲜事列表
     */
    @GetMapping("/all/by_time")
    public ResultVo<List<FreshNewsDetailResp>> getAllFreshNews(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllFreshNews(page, pageSize);
    }

    // 获取所有按点赞排序的新鲜事
    @GetMapping("/all/by_likes")
    public ResultVo<List<FreshNewsDetailResp>> getAllByLikes(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllByLikes(page, pageSize);
    }

    // 根据标签获取新鲜事
    @GetMapping("/tags/{tag}")
    public ResultVo<List<FreshNewsDetailResp>> getByTag(@PathVariable("tag") String tag,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getByTag(tag, page, pageSize);
    }
}
