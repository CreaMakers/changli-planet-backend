package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/fresh_news")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{fresh_news_id}/likes/{user_id}")
    public ResultVo<Void> likeNews(@PathVariable("fresh_news_id") Long newsId,
                                   @PathVariable("user_id") Long userId) {
        return likeService.likeNews(newsId, userId);
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @PostMapping("/comments/{comment_id}/likes/{user_id}")
    public ResultVo<Void> likeComment(@PathVariable("comment_id") Long commentId,
                                      @PathVariable("user_id") Long userId) {
        return likeService.likeComment(commentId, userId);
    }

    /**
     * 刷新所有新鲜事的点赞数量
     * @return 结果
     */
    @PostMapping("/refresh_like_count")
    public ResultVo<Void> refreshAllNewsLikeCount() {
        return likeService.refreshNewsLikeNum() ;
    }

    /**
     * 刷新所有评论的点赞数量
     * @return 结果
     */
    @PostMapping("/comment/refresh_like_count")
    public ResultVo<Void> refreshAllCommentLikeCount() {
        return likeService.refreshCommentLikeNum();
    }
}
