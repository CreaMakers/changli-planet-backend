package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;

import java.util.List;

public interface CommentService {
    ResultVo<Void> addComment(Long freshNewsId, FreshNewsCommentRequest freshNewsCommentRequest);

    ResultVo<List<FreshNewsCommentResp>> listComments(Long freshNewsId, Integer page, Integer pageSize);

    ResultVo<Void> deleteComment(Long commentId, Long userId);

    ResultVo<List<FreshNewsCommentResp>> listReplies(Long commentId, Integer page, Integer pageSize);

    ResultVo<Void> addReply(Long commentId, FreshNewsCommentRequest freshNewsCommentRequest);
}
