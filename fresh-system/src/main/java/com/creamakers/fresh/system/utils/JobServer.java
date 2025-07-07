package com.creamakers.fresh.system.utils;

import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.creamakers.fresh.system.constants.CommonConst.NEWS_LIKE_COUNT_UPDATE_FAILED_MESSAGE;

/**
 * @author lihongwei
 * @date 2025/5/30
 * @Description 定时任务刷新点赞数
 */
@Component
@Slf4j
public class JobServer {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    
    @Autowired
    private FreshNewsMapper freshNewsMapper;

    @Scheduled(fixedRate = 60 * 1000 * 10)
    public void refreshAllNewsLikeCount() {
        long startTime = System.currentTimeMillis();
        log.info("开始获取redis点赞键值对");
        Set<String> keys = redisTemplate.keys(RedisKeyConstant.LIKE_NEWS_NUM + "*");

        if (CollectionUtils.isEmpty(keys)) {
            log.info("没有需要刷新的点赞数据");
            return;
        }

        log.info("找到{}个需要处理的键", keys.size());

        List<FreshNews> freshNews = new ArrayList<>();

        List<String>deleteKeys = new ArrayList<>();

        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Long expire = redisTemplate.getExpire(key);
                if (expire != null && expire > 0||expire == -1) {
                    Date expireTime = new Date(System.currentTimeMillis() + expire);
                    // 如果过期时间小于10分钟，则刷新点赞数
                    if (expireTime.before(new Date(System.currentTimeMillis() + (10 * 60 * 1000)))) {
                        Long freshNewsId = Long.valueOf(key.substring(key.lastIndexOf(":") + 1));
                        Integer likeNum = (Integer) redisTemplate.opsForValue().get(key);
                        if (likeNum == null || likeNum == 0) {
                            continue;
                        }
                        FreshNews post = new FreshNews().setFreshNewsId(freshNewsId).setLiked(likeNum).setUpdateTime(LocalDateTime.now());
                        freshNews.add(post);
                        deleteKeys.add(key);
                    }
                }
            }

            if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(freshNews)) {
                int rows = freshNewsMapper.updateFreshNewsLiked(freshNews);
                if (rows != freshNews.size()) {
                    ResultVo.fail(NEWS_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
                redisTemplate.delete(deleteKeys);
            }
            log.info("刷新点赞数完成，耗时{}毫秒", System.currentTimeMillis() - startTime);
        }
    }
}
