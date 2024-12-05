package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.PostCommentResp;
import com.creamakers.websystem.domain.vo.response.PostResp;
import com.creamakers.websystem.domain.vo.response.ReportPostResp;

import java.util.List;

public interface PostService {
    ResultVo<List<PostResp>> getAllPosts(Integer page, Integer pageSize);

    ResultVo<PostResp> getPostById(Long postId);


    ResultVo<Void> deletePostById(Long postId);

    ResultVo<List<ReportPostResp>> getAllReportedPosts(Integer page, Integer pageSize);

    ResultVo<List<PostCommentResp>> getAllCommentsByPostId(Long postId, Integer page, Integer pageSize);

    ResultVo<List<PostCommentResp>> searchCommentsByKeyWord(Long postId, String keyWord, Integer page, Integer pageSize);

    ResultVo<Void> deleteCommentByPostIdAndCommentId(Long postId, Long commentId);
}
