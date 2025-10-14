package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCheckResp;

import java.io.IOException;
import java.util.List;

public interface FreshNewsCheckService {
    ResultVo<List<FreshNewsCheckResp>> findAllFreshNewsCheck(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsCheckResp>> findFreshNewsCheckByQuery(Long freshNewsCheckId, Long freshNewsId, Integer checkStatus, Integer page, Integer pageSize);

    ResultVo<Void> checkFreshNews(Long freshNewsCheckId, Integer checkStatus);

    boolean addFreshNewsCheck(FreshNews freshNews, String finalUrls) throws IOException;
}
