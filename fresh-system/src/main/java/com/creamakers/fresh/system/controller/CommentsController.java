package com.creamakers.fresh.system.controller;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsChildCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsFatherCommentResp;
import com.creamakers.fresh.system.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/fresh_news/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论(一级评论)
     */
    @PostMapping("/fresh_news/add")
    public ResultVo<Long> addComment(@RequestBody FreshNewsCommentRequest freshNewsCommentRequest) {
        return commentService.addComment(freshNewsCommentRequest);
    }

    /**
     * 获取新鲜事的评论列表(一级评论)
     * @param freshNewsId 新鲜事ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    @GetMapping("/{fresh_news_id}/list")
    public ResultVo<FreshNewsCommentResp<FreshNewsFatherCommentResp>> listComments(@PathVariable("fresh_news_id") Long freshNewsId,
                                                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                   @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(value = "user_id") Integer userId) {
        return commentService.listComments(freshNewsId, page, pageSize, userId);
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param isParent 是否是父评论 (0:子评论, 1:父评论)
     * @return 结果
     */
    @DeleteMapping("/{comment_id}/{is_parent}")
    public ResultVo<Void> deleteComment(@PathVariable("comment_id") Long commentId,
                                        @PathVariable("is_parent") Integer isParent) {
        return commentService.deleteComment(commentId, isParent);
    }

    /**
     * 获取评论的回复列表(获取子评论)
     * @param freshNewsId 新鲜事ID
     * @param commentId 评论ID(父评论ID)
     * @param page 页码
     * @param pageSize 每页大小
     * @return 回复列表
     */
    @GetMapping("/{fresh_news_id}/replies")
    public ResultVo<FreshNewsCommentResp<FreshNewsChildCommentResp>> listReplies(@PathVariable(value = "fresh_news_id") Long freshNewsId,
                                                                 @RequestParam(value = "comment_id") Long commentId,
                                                                 @RequestParam(value = "user_id") Integer userId,
                                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        return commentService.listReplies(freshNewsId,commentId, userId, page, pageSize);
    }

    /**
     * 添加评论回复
     */
    @PostMapping("/replies/add")
    public ResultVo<Long> addReply(@RequestBody FreshNewsCommentRequest freshNewsCommentRequest) {
        return commentService.addReply(freshNewsCommentRequest);
    }
}