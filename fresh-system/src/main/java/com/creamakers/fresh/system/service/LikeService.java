package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;

public interface LikeService {
    ResultVo<Void> likeComment(Long commentId, Long userId, Integer isParent);

    ResultVo<Void> likeNews(Long newsId, Long userId);

    ResultVo<Void> refreshCommentLikeNum();

    ResultVo<Void> refreshNewsLikeNum();
}
