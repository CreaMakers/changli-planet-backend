package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsChildCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsFatherCommentResp;

import java.util.List;

public interface CommentService {
    ResultVo<Long> addComment(FreshNewsCommentRequest freshNewsCommentRequest);

    ResultVo<FreshNewsCommentResp<FreshNewsFatherCommentResp>> listComments(Long freshNewsId, Integer page, Integer pageSize, Integer userId);

    ResultVo<Void> deleteComment(Long commentId, Integer isParent);

    ResultVo<FreshNewsCommentResp<FreshNewsChildCommentResp>> listReplies(Long freshNewsId, Long commentId, Integer userId, Integer page, Integer pageSize);

    ResultVo<Long> addReply(FreshNewsCommentRequest freshNewsCommentRequest);
}
