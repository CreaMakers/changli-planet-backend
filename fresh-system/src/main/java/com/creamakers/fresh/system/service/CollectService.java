package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.dto.FreshNewsFavorites;
import com.creamakers.fresh.system.domain.vo.ResultVo;

import java.util.List;

public interface CollectService {
    ResultVo<Void> collectNews(Long userId, Long newsId);

    ResultVo<List<FreshNewsFavorites>> listFavorites(Long userId, Integer page, Integer pageSize);

    ResultVo<Void> deleteFavorite(Long userId, Long newsId);
}
