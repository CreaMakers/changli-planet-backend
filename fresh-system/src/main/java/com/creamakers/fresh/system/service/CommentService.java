package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsChildCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;

import java.util.List;

public interface CommentService {
    ResultVo<Void> addComment(FreshNewsCommentRequest freshNewsCommentRequest);

    ResultVo<FreshNewsCommentResp> listComments(Long freshNewsId, Integer page, Integer pageSize);

    ResultVo<Void> deleteComment(Long freshNewsId, Long commentId, Integer isParent);

    ResultVo<List<FreshNewsChildCommentResp>> listReplies(Long freshNewsId, Long commentId, Integer page, Integer pageSize);

    ResultVo<Void> addReply(FreshNewsCommentRequest freshNewsCommentRequest);
}
