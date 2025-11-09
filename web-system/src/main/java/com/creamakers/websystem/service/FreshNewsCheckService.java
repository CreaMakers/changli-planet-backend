package com.creamakers.websystem.service;



import com.creamakers.websystem.domain.dto.FreshNews;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.FreshNewsCheckResp;

import java.io.IOException;
import java.util.List;

public interface FreshNewsCheckService {
    ResultVo<List<FreshNewsCheckResp>> findAllFreshNewsCheck(Integer page, Integer pageSize);

    ResultVo<List<FreshNewsCheckResp>> findFreshNewsCheckByQuery(Long freshNewsCheckId, Long freshNewsId, Integer checkStatus, Integer page, Integer pageSize);

    ResultVo<Void> checkFreshNews(Long freshNewsCheckId, Integer checkStatus);

    ResultVo<Void> deleteFreshNewsCheck(Long freshNewsCheckId);
}
