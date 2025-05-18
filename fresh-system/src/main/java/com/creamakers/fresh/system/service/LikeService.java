package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;

public interface LikeService {
    ResultVo<Boolean> likeComment(Long commentId, Long userId);

    ResultVo<Boolean> likeNews(Long newsId, Long userId);

    ResultVo<Void> refreshCommentLikeNum();

    ResultVo<Void> refreshNewsLikeNum();
}
