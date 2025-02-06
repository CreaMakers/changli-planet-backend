package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;

import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;
import com.creamakers.fresh.system.service.FreshNewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/fresh_news")
public class FreshNewsController {

    @Autowired
    private FreshNewsService freshNewsService;

    /**
     * 创建新鲜事
     * @param freshNewsRequest 请求参数
     * @return 结果
     */
    @PostMapping
    public ResultVo<Void> createFreshNews(@RequestBody FreshNewsRequest freshNewsRequest) {
        return freshNewsService.createFreshNews(freshNewsRequest);
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
    public ResultVo<List<FreshNewsResp>> getAllFreshNews(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllFreshNews(page, pageSize);
    }

    // 获取所有按点赞排序的新鲜事
    @GetMapping("/all/by_likes")
    public ResultVo<List<FreshNewsResp>> getAllByLikes(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getAllByLikes(page, pageSize);
    }

    // 根据标签获取新鲜事
    @GetMapping("/tags/{tag}")
    public ResultVo<List<FreshNewsResp>> getByTag(@PathVariable("tag") String tag,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return freshNewsService.getByTag(tag, page, pageSize);
    }
}
