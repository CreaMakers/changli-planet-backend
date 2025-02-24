package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.dto.FreshNewsComment;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;
import com.creamakers.fresh.system.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/fresh_news/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     */
    @PostMapping("/{fresh_news_id}/add")
    public ResultVo<Void> addComment(@PathVariable("fresh_news_id") Long freshNewsId,
                                     @RequestBody FreshNewsCommentRequest freshNewsCommentRequest) {
        return commentService.addComment(freshNewsId,freshNewsCommentRequest);
    }

    /**
     * 获取新鲜事的评论列表
     * @param freshNewsId 新鲜事ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    @GetMapping("/{fresh_news_id}/list")
    public ResultVo<List<FreshNewsCommentResp>> listComments(@PathVariable("fresh_news_id") Long freshNewsId,
                                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                             @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return commentService.listComments(freshNewsId, page, pageSize);
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/{comment_id}/{user_id}")
    public ResultVo<Void> deleteComment(@PathVariable("comment_id") Long commentId,
                                        @PathVariable("user_id") Long userId) {
        return commentService.deleteComment(commentId, userId);
    }

    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 回复列表
     */
    @GetMapping("/{comment_id}/replies")
    public ResultVo<List<FreshNewsCommentResp>> listReplies(@PathVariable("comment_id") Long commentId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return commentService.listReplies(commentId, page, pageSize);
    }

    /**
     * 添加评论回复
     */
    @PostMapping("/{comment_id}/replies/add")
    public ResultVo<Void> addReply(@PathVariable("comment_id") Long commentId,
                                   @RequestBody FreshNewsCommentRequest freshNewsCommentRequest) {
        return commentService.addReply(commentId, freshNewsCommentRequest);
    }
}