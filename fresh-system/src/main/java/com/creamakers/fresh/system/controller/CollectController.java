package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.dto.FreshNewsFavorites;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/fresh_news/favorites")
public class CollectController {

    @Autowired
    private CollectService favoritesService;

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param newsId 新鲜事ID
     * @return 结果
     */
    @PostMapping("/add/{user_id}/{news_id}")
    public ResultVo<Void> CollectNews(@PathVariable("user_id") Long userId, @PathVariable("news_id") Long newsId) {
        return favoritesService.CollectNews(userId, newsId);
    }

    /**
     * 获取用户收藏的新鲜事列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 收藏的新鲜事列表
     */
    @GetMapping("/list")
    public ResultVo<List<FreshNewsFavorites>> listFavorites(@RequestParam("user_id") Long userId,
                                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return favoritesService.listFavorites(userId, page, pageSize);
    }

    /**
     * 删除收藏
     * @param userId 用户ID
     * @param newsId 新鲜事ID
     * @return 结果
     */
    @DeleteMapping("/delete/{user_id}/{news_id}")
    public ResultVo<Void> deleteFavorite(@PathVariable("user_id") Long userId, @PathVariable("news_id") Long newsId) {
        return favoritesService.deleteFavorite(userId, newsId);
    }
}
