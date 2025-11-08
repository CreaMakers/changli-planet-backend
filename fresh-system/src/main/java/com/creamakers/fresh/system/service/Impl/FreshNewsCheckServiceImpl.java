package com.creamakers.fresh.system.service.Impl;

import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.dao.FreshNewsCheckMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsCheck;
import com.creamakers.fresh.system.service.FreshNewsCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FreshNewsCheckServiceImpl implements FreshNewsCheckService {
    @Autowired
    private FreshNewsCheckMapper freshNewsCheckMapper;

    // 新增新鲜事审核记录
    @Override
    public boolean addFreshNewsCheck(FreshNews freshNews, String finalUrls)throws IOException {
        FreshNewsCheck freshNewsCheck = new FreshNewsCheck()
                .setFreshNewsId(freshNews.getFreshNewsId())
                .setTitle(freshNews.getTitle())
                .setContent(freshNews.getContent())
                .setImageUrl(finalUrls)
                .setCheckStatus(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setIsDeleted(0)
                .setCheckTime(null);
        int insert = freshNewsCheckMapper.insert(freshNewsCheck);
        if(insert <= 0){
            // 新增新鲜事审核记录失败，抛出异常
            throw new IOException(CommonConst.NEWS_CHECK_FAILED_MESSAGE);
        }
        log.info("新增新鲜事审核记录，审核记录ID：{}，新鲜事ID：{}", freshNewsCheck.getFreshNewsCheckId(),freshNewsCheck.getFreshNewsId());
        return true;
    }
}
