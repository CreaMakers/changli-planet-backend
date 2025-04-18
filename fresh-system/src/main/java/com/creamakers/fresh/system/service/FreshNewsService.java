package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FreshNewsService {
    ResultVo<FreshNewsDetailResp> createFreshNews(List<MultipartFile> images, FreshNewsRequest freshNewsRequest) throws IOException;

    ResultVo<FreshNewsDetailResp> getFreshNewsById(Long freshNewsId);

    ResultVo<List<FreshNewsDetailResp>> getAllFreshNews(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsDetailResp>> getAllByLikes(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsDetailResp>> getByTag(String tag, Integer page, Integer pageSize);
}
