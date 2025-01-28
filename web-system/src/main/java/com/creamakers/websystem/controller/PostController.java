package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.dto.ReportPost;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.request.ReviewPostReq;
import com.creamakers.websystem.domain.vo.response.PostCommentResp;
import com.creamakers.websystem.domain.vo.response.PostResp;
import com.creamakers.websystem.domain.vo.response.ReportPostResp;
import com.creamakers.websystem.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/posts")
public class PostController {
    @Autowired
    private PostService postService;
    // 获取所有帖子
    @GetMapping
    public ResultVo<List<PostResp>> getAllPosts(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize) {
        return postService.getAllPosts(page,pageSize);
    }

    // 获取特定帖子的详细信息
    @GetMapping("/{postId}")
    public ResultVo<PostResp> getPostById(@PathVariable("postId") Long postId) {
       return postService.getPostById(postId);
    }

    // 删除帖子
    @DeleteMapping("/{postId}")
    public ResultVo<Void> deletePostById(@PathVariable Long postId) {
        return postService.deletePostById(postId);
    }
    //获取被举报的帖子列表（审核举报内容）
    @GetMapping("/reported")
    public ResultVo<List<ReportPostResp>>getReportedPosts(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize) {
        return postService.getAllReportedPosts(page,pageSize);
    }
    //查看帖子的所有评论
    @GetMapping("/{post_id}/comments")
    public ResultVo<List<PostCommentResp>>getAllCommentsByPostId(@PathVariable("post_id")Long postId,
                                                                 @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize){
        return postService.getAllCommentsByPostId(postId,page,pageSize);
    }
    //搜索某个评论
    @GetMapping("/{post_id}/comments/search")
    public ResultVo<List<PostCommentResp>> searchCommentsByKeyWord(@PathVariable("post_id") Long postId,
                                                                   @RequestParam(value = "keyword") String keyWord,
                                                                   @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10")  Integer pageSize){
        return postService.searchCommentsByKeyWord(postId,keyWord,page,pageSize);
    }
    //删除特定评论
    @DeleteMapping("/{post_id}/comments/{comment_id}")
    public ResultVo<Void> deleteCommentByPostIdAndCommentId(@PathVariable("post_id") Long postId,
                                                            @PathVariable("comment_id")Long commentId){
        return postService.deleteCommentByPostIdAndCommentId(postId,commentId);
    }

    @PutMapping("/{post_id}/review")
    public  ResultVo<ReportPost> reviewReportPostByPostId(@PathVariable("post_id") Long postId, @RequestBody ReviewPostReq reviewPostReq){
        return postService.reviewReportPostByPostId(postId,reviewPostReq);
    }
}

