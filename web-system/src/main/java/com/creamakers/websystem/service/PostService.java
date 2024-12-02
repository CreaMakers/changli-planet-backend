package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.PostResp;

import java.util.List;

public interface PostService {
    ResultVo<List<PostResp>> getAllPosts(Integer page, Integer pageSize);

    ResultVo<PostResp> getPostById(Long postId);


    ResultVo<Void> deletePostById(Long postId);
}
