package com.creamakers.websystem.service;

import com.creamakers.websystem.domain.vo.ResultVo;

public interface FreshNewsCommentService {
    boolean deleteCommentByFreshNewsId(Long freshNewsId);

    ResultVo<Void> deleteComment(Long commentId, Integer isParent);
}
