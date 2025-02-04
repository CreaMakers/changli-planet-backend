package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;

import java.util.List;

public interface FreshNewsService {
    ResultVo<Void> createFreshNews(FreshNewsRequest freshNewsRequest);

    ResultVo<FreshNewsDetailResp> getFreshNewsById(Long freshNewsId);

    ResultVo<List<FreshNewsResp>> getAllFreshNews(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsResp>> getAllByLikes(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsResp>> getByTag(String tag, Integer page, Integer pageSize);

    ResultVo<Void> likeFreshNews(Long freshNewsId, Long userId);

    ResultVo<Void> unlikeFreshNews(Long freshNewsId, Long userId);
}
