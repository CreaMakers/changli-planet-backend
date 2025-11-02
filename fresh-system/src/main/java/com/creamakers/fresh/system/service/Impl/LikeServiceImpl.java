package com.creamakers.fresh.system.service.Impl;
import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.creamakers.fresh.system.dao.FreshNewsChildCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsFatherCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsChildComment;
import com.creamakers.fresh.system.domain.dto.FreshNewsFatherComment;
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
    private FreshNewsFatherCommentMapper freshNewsFatherCommentMapper;  // 用于访问数据库中的评论数据（父评论）

    @Autowired
    private FreshNewsChildCommentMapper freshNewsChildCommentMapper;  // 用于访问数据库中的评论数据（子评论）

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
     * @param isParent 是否是父评论（0：子评论，1：父评论）
     * @return 结果
     */
    @Override
    public ResultVo<Void> likeComment(Long commentId, Long userId, Integer isParent) {
        // 构建Redis键，区分子评论和父评论
        String userSetKey = RedisKeyConstant.LIKE_COMMENT + commentId + ":" + isParent; // 用于SET操作
        String countKey = RedisKeyConstant.LIKE_COMMENT_NUM + commentId + ":" + isParent; // 用于计数

        // 检查该评论是否已经点赞
        Boolean isLiked = redisTemplate.opsForSet().isMember(userSetKey, userId);

        if (isLiked != null && !isLiked) {
            // 如果没有点赞，则进行点赞
            redisTemplate.opsForSet().add(userSetKey, userId);
            // 使用单独的key增加评论的点赞数
            redisTemplate.opsForValue().increment(countKey);

            rabbitTemplate.convertAndSend("likeCommentExchange", "likeComment", commentId+":"+isParent);
        } else {
            // 如果已经点赞，再次点击则取消点赞
            redisTemplate.opsForSet().remove(userSetKey, userId);
            // 使用单独的key减少评论的点赞数
            redisTemplate.opsForValue().decrement(countKey);
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
        Set<String> fatherKeys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*:1");
        Set<String> childKeys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*:0");
        List<FreshNewsFatherComment> fatherComments = new ArrayList<>();
        List<FreshNewsChildComment>  childComments = new ArrayList<>();
        List<String> deleteKeys = new ArrayList<>();

        // 处理父评论
        if (!CollectionUtils.isEmpty(fatherKeys)) {
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
            if (!CollectionUtils.isEmpty(fatherComments)) {
                int rows = freshNewsFatherCommentMapper.updateCommentLikeNum(fatherComments);
                if (rows != fatherComments.size()) {
                    return ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
            }
        }

        // 处理子评论
        if (!CollectionUtils.isEmpty(childKeys)) {
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
            if (!CollectionUtils.isEmpty(childComments)) {
                int rows = freshNewsChildCommentMapper.updateCommentLikeNum(childComments);
                if (rows != childComments.size()) {
                    return ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
                }
            }
        }

        // 删除过期的key
        if(!CollectionUtils.isEmpty(deleteKeys)){
            redisTemplate.delete(deleteKeys);
        }
        return ResultVo.success();  // 返回成功

//        Set<String> keys = redisTemplate.keys(RedisKeyConstant.LIKE_COMMENT_NUM + "*");
//        List<FreshNewsComment> comments = new ArrayList<>();
//        List<String> deleteKeys = new ArrayList<>();
//
//        if (!CollectionUtils.isEmpty(keys)) {
//            for (String key : keys) {
//                Long expire = redisTemplate.getExpire(key);
//                if (expire != null && expire > 0) {
//                    Date expireTime = new Date(System.currentTimeMillis() + expire);
//                    // 如果过期时间小于10分钟，则刷新点赞数
//                    if (expireTime.before(new Date(System.currentTimeMillis() + (10 * 60 * 1000)))) {
//                        Long commentId = Long.valueOf(key.substring(key.lastIndexOf(":") + 1));
//                        Integer likeNum = (Integer) redisTemplate.opsForValue().get(key);
//                        if (likeNum == null || likeNum == 0) {
//                            continue;
//                        }
//                        FreshNewsComment comment = new FreshNewsComment().setCommentId(commentId).setLiked(likeNum).setUpdateTime(LocalDateTime.now());
//                        comments.add(comment);
//                        deleteKeys.add(key);
//                    }
//                }
//            }
//            if (!CollectionUtils.isEmpty(comments)) {
//                int rows = freshNewsCommentMapper.updateCommentLikeNum(comments);
//                if (rows != comments.size()) {
//                    ResultVo.fail(COMMENT_LIKE_COUNT_UPDATE_FAILED_MESSAGE);
//                }
//                redisTemplate.delete(deleteKeys);
//            }
//        }
//        return ResultVo.success();  // 返回成功
    }
}
