package com.creamakers.fresh.system.service.Impl;
import com.creamakers.fresh.system.constants.RedisKeyConstant;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.creamakers.fresh.system.dao.FreshNewsCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsComment;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.service.LikeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.creamakers.fresh.system.constants.CommonConst.COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE;
import static com.creamakers.fresh.system.constants.CommonConst.NEWS_LIKE_COUNT_UPDATE_FAILED_MESSAGE;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FreshNewsMapper freshNewsMapper;  // 用于访问数据库中的新闻/帖子数据
    @Autowired
    private FreshNewsCommentMapper freshNewsCommentMapper; // 用于访问数据库中的评论数据
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 点赞新闻
     *
     * @param newsId  新鲜事ID
     * @param userId  用户ID
     * @return 结果
     */
    @Override
    public ResultVo<Void> likeNews(Long newsId, Long userId) {
        // 检查该新闻是否已经点赞
        Boolean isLiked = redisTemplate.opsForSet().isMember(RedisKeyConstant.LIKE_NEWS + newsId, userId);

        if (isLiked != null && !isLiked) {
            // 如果没有点赞，则进行点赞
            redisTemplate.opsForSet().add(RedisKeyConstant.LIKE_NEWS + newsId, userId);
            // 增加新闻的点赞数
            redisTemplate.opsForValue().increment(RedisKeyConstant.LIKE_NEWS_NUM + newsId);

            rabbitTemplate.convertAndSend("likeNewsExchange", "likeNews",newsId);
        } else {
            // 如果已经点赞，再次点击则取消点赞
            redisTemplate.opsForSet().remove(RedisKeyConstant.LIKE_NEWS + newsId, userId);
            // 减少新闻的点赞数
            redisTemplate.opsForValue().decrement(RedisKeyConstant.LIKE_NEWS_NUM + newsId);
        }

        return ResultVo.success(null);  // 返回成功
    }

    /**
     * 刷新新鲜事的点赞数量
     * 从Redis中获取新闻的点赞数并刷新到数据库
     *
     * @return 结果
     */
    @Override
    @Transactional
    public ResultVo<Void> refreshNewsLikeNum() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstant.LIKE_NEWS_NUM + "*");
        List<FreshNews> freshNews = new ArrayList<>();
        List<String> deleteKeys = new ArrayList<>();

        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Long expire = redisTemplate.getExpire(key);
                if (expire != null && expire > 0) {
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
            if (!CollectionUtils.isEmpty(freshNews)) {
                int rows = freshNewsMapper.updateFreshNewsLiked(freshNews);
                if (rows != freshNews.size()) {
                    ResultVo.fail(NEWS_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
                redisTemplate.delete(deleteKeys);
            }
        }
        return ResultVo.success();  // 返回成功
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId   用户ID
     * @return 结果
     */
    @Override
    public ResultVo<Void> likeComment(Long commentId, Long userId) {
        // 检查该评论是否已经点赞
        Boolean isLiked = redisTemplate.opsForSet().isMember(RedisKeyConstant.LIKE_COMMENT + commentId, userId);

        if (isLiked != null && !isLiked) {
            // 如果没有点赞，则进行点赞
            redisTemplate.opsForSet().add(RedisKeyConstant.LIKE_COMMENT + commentId, userId);
            // 增加评论的点赞数
            redisTemplate.opsForValue().increment(RedisKeyConstant.LIKE_COMMENT_NUM + commentId);

            rabbitTemplate.convertAndSend("likeCommentExchange", "likeComment",commentId);
        } else {
            // 如果已经点赞，再次点击则取消点赞
            redisTemplate.opsForSet().remove(RedisKeyConstant.LIKE_COMMENT + commentId, userId);
            // 减少评论的点赞数
            redisTemplate.opsForValue().decrement(RedisKeyConstant.LIKE_COMMENT_NUM + commentId);
        }

        return ResultVo.success();  // 返回成功
    }

    /**
     * 刷新评论的点赞数量
     * 从Redis中获取评论的点赞数并刷新到数据库
     *
     * @return 结果
     */
    @Override
    @Transactional
    public ResultVo<Void> refreshCommentLikeNum() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*");
        List<FreshNewsComment> comments = new ArrayList<>();
        List<String> deleteKeys = new ArrayList<>();

        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                Long expire = redisTemplate.getExpire(key);
                if (expire != null && expire > 0) {
                    Date expireTime = new Date(System.currentTimeMillis() + expire);
                    // 如果过期时间小于10分钟，则刷新点赞数
                    if (expireTime.before(new Date(System.currentTimeMillis() + (10 * 60 * 1000)))) {
                        Long commentId = Long.valueOf(key.substring(key.lastIndexOf(":") + 1));
                        Integer likeNum = (Integer) redisTemplate.opsForValue().get(key);
                        if (likeNum == null || likeNum == 0) {
                            continue;
                        }
                        FreshNewsComment comment = new FreshNewsComment().setCommentId(commentId).setLiked(likeNum).setUpdateTime(LocalDateTime.now());
                        comments.add(comment);
                        deleteKeys.add(key);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(comments)) {
                int rows = freshNewsCommentMapper.updateCommentLikeNum(comments);
                if (rows != comments.size()) {
                    ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
                redisTemplate.delete(deleteKeys);
            }
        }
        return ResultVo.success();  // 返回成功
    }
}
