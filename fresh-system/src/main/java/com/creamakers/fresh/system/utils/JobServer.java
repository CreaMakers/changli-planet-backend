package com.creamakers.fresh.system.utils;

import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.dao.FreshNewsChildCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsFatherCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsChildComment;
import com.creamakers.fresh.system.domain.dto.FreshNewsFatherComment;
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

import static com.creamakers.fresh.system.constants.CommonConst.COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE;
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

    @Autowired
    private FreshNewsChildCommentMapper freshNewsChildCommentMapper;

    @Autowired
    private FreshNewsFatherCommentMapper freshNewsFatherCommentMapper;

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
                        Object value = redisTemplate.opsForValue().get(key);
                        if (value == null) {
                            continue;
                        }
                        Long likeNum = Long.valueOf(value.toString());
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

    @Scheduled(fixedRate = 60 * 1000 * 10)
    public void refreshAllNewsCommentLikeCount() {
        long startTime = System.currentTimeMillis();
        log.info("开始获取redis评论点赞键值对");
        Set<String> fatherKeys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*:1");
        Set<String> childKeys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*:0");
        List<FreshNewsFatherComment> fatherComments = new ArrayList<>();
        List<FreshNewsChildComment>  childComments = new ArrayList<>();
        List<String> deleteKeys = new ArrayList<>();

        // 处理父评论
        if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(fatherKeys)) {
            for (String key : fatherKeys) {
                Long expire = redisTemplate.getExpire(key);
                if (expire != null && expire > 0) {
                    Date expireTime = new Date(System.currentTimeMillis() + expire);
                    // 如果过期时间小于10分钟，则刷新点赞数
                    if (expireTime.before(new Date(System.currentTimeMillis() + (10 * 60 * 1000)))) {
                        //Long commentId = Long.valueOf(key.substring(key.lastIndexOf(":") + 1));
                        Long commentId = Long.valueOf(key.split(":")[1]);               // key结构 likeCommentNum:commentId:isParent
                        Integer likeNum = (Integer) redisTemplate.opsForValue().get(key);
                        if (likeNum == null || likeNum == 0) {
                            continue;
                        }
                        FreshNewsFatherComment comment = new FreshNewsFatherComment().setId(commentId).setLikedCount(likeNum).setUpdateTime(LocalDateTime.now());
                        fatherComments.add(comment);
                        deleteKeys.add(key);
                    }
                }
            }
            if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(fatherComments)) {
                int rows = freshNewsFatherCommentMapper.updateCommentLikeNum(fatherComments);
                if (rows != fatherComments.size()) {
                    ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
            }
        }

        // 处理子评论
        if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(childKeys)) {
            for (String key : childKeys) {
                Long expire = redisTemplate.getExpire(key);
                if (expire != null && expire > 0) {
                    Date expireTime = new Date(System.currentTimeMillis() + expire);
                    // 如果过期时间小于10分钟，则刷新点赞数
                    if (expireTime.before(new Date(System.currentTimeMillis() + (10 * 60 * 1000)))) {
                        //Long commentId = Long.valueOf(key.substring(key.lastIndexOf(":") + 1));
                        Long commentId = Long.valueOf(key.split(":")[1]);               // key结构 likeCommentNum:commentId:isParent
                        Integer likeNum = (Integer) redisTemplate.opsForValue().get(key);
                        if (likeNum == null || likeNum == 0) {
                            continue;
                        }
                        FreshNewsChildComment comment = new FreshNewsChildComment().setId(commentId).setLikedCount(likeNum).setUpdateTime(LocalDateTime.now());
                        childComments.add(comment);
                        deleteKeys.add(key);
                    }
                }
            }
            if (!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(childComments)) {
                int rows = freshNewsChildCommentMapper.updateCommentLikeNum(childComments);
                if (rows != childComments.size()) {
                    ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
            }
        }

        // 删除过期的key
        if(!com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(deleteKeys)){
            redisTemplate.delete(deleteKeys);
        }
        log.info("刷新点赞数完成，耗时{}毫秒", System.currentTimeMillis() - startTime);
    }
}
