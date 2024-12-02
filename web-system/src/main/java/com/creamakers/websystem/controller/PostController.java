package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.PostResp;
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
}
