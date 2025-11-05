package com.creamakers.fresh.system.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.dao.FreshNewsChildCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsFatherCommentMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.dao.UserMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsChildComment;
import com.creamakers.fresh.system.domain.dto.FreshNewsFatherComment;
import com.creamakers.fresh.system.domain.dto.User;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsChildCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsFatherCommentResp;
import com.creamakers.fresh.system.service.CommentService;
import org.apache.velocity.util.ArrayListWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.creamakers.fresh.system.constants.CommonConst.*;

@Service
public class CommentServiceImpl implements CommentService {

//    @Autowired
//    private FreshNewsCommentMapper freshNewsCommentMapper;

    @Autowired
    private FreshNewsFatherCommentMapper freshNewsFatherCommentMapper;

    @Autowired
    private FreshNewsChildCommentMapper freshNewsChildCommentMapper;

    @Autowired
    private FreshNewsMapper freshNewsMapper;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    // redis记录浏览量
    private static final String VIEW_COUNT_ZSET_KEY = "freshNews:viewCounts:zset";  // 定义 ZSET 键名
    private static final long EXPIRATION = 7L * 24 * 60 * 60; // 7天，单位为秒

    /**
     * 添加评论(非回复评论)
     *
     * @param freshNewsCommentRequest 评论添加请求
     * @return 结果
     */
    @Override
    public ResultVo<Long> addComment(FreshNewsCommentRequest freshNewsCommentRequest) {
        User user = userMapper.selectById(freshNewsCommentRequest.getUserId());
        if (user == null) {
            // 用户不存在
            return ResultVo.fail(BAD_REQUEST_CODE, USER_NOT_FOUND_MESSAGE);
        }

        if (freshNewsCommentRequest.getParentId() != 0L) {
            // 不是父评论,返回错误
            return ResultVo.fail(BAD_REQUEST_CODE, NOT_FATHER_COMMENT);
        }
        FreshNewsFatherComment freshNewsFatherComment = new FreshNewsFatherComment()
                .setFreshNewsId(freshNewsCommentRequest.getNewsId())    // 关联的新鲜事ID
                .setLikedCount(0)                                       // 点赞数量
                .setChildCount(0)                                       // 子评论数量
                .setContent(freshNewsCommentRequest.getContent())       // 评论内容
                .setUserId(freshNewsCommentRequest.getUserId())         // 用户ID
                .setUserName(user.getAccount() == null ? "长理学子" : user.getAccount())   // 用户名
                .setUserAvatar(user.getAvatarUrl())                     // 用户头像URL
                .setCommentIp(freshNewsCommentRequest.getCommentIp())   // 评论发布的地址
                .setCommentTime(LocalDateTime.now())                    // 评论发布的时间
                .setIsActive(0)                                         // 评论是否有效: 0-未有效, 1-已有效
                .setIsDeleted(0)                                        // 评论是否删除: 0-未删除, 1-已删除
                .setCreateTime(LocalDateTime.now())                     // 创建时间
                .setUpdateTime(LocalDateTime.now());                    // 更新时间
        int result = freshNewsFatherCommentMapper.insert(freshNewsFatherComment);
        if (result > 0) {
            // 更新新鲜事的评论数量
            FreshNews freshNews = freshNewsMapper.selectById(freshNewsCommentRequest.getNewsId());
            freshNews.setComments(freshNews.getComments() + 1);
            freshNewsMapper.updateById(freshNews);

            rabbitTemplate.convertAndSend("commentExchange", "comment", freshNewsFatherComment);
            return ResultVo.success(freshNewsFatherComment.getId());
        } else {
            return ResultVo.fail(COMMENT_ADD_FAILED_MESSAGE);
        }

//        FreshNewsComment comment = new FreshNewsComment();
//        comment.setNewsId(freshNewsCommentRequest.getNewsId());
//        comment.setUserId(freshNewsCommentRequest.getUserId());
//        comment.setContent(freshNewsCommentRequest.getContent());
//        comment.setParentId(0L);  // 一级评论的 parent_id 为 0
//
//        int result = freshNewsCommentMapper.insert(comment);
//        if (result > 0) {
//            Long commentId = comment.getCommentId();
//            // 更新 root 字段
//            FreshNewsComment updateComment = new FreshNewsComment();
//            updateComment.setCommentId(commentId);
//            updateComment.setRoot(commentId);  // 设置 root 字段为评论的 ID
//            freshNewsCommentMapper.updateById(updateComment);  // 使用 MyBatis-Plus 更新
//            rabbitTemplate.convertAndSend("commentExchange", "comment", updateComment);
//            return ResultVo.success();
//        } else {
//            return ResultVo.fail(COMMENT_ADD_FAILED_MESSAGE);
//        }
    }

    /**
     * 获取新鲜事下的所有一级评论
     *
     * @param freshNewsId 新鲜事ID
     * @param page        页码
     * @param pageSize    每页大小
     * @return 评论列表
     */
    @Override
    public ResultVo<FreshNewsCommentResp<FreshNewsFatherCommentResp>> listComments(Long freshNewsId, Integer page, Integer pageSize, Integer userId) {
        // 构造父评论查询参数
        Page<FreshNewsFatherComment> pageParam = new Page<>(page, pageSize);
        QueryWrapper<FreshNewsFatherComment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("fresh_news_id", freshNewsId)
                .eq("is_deleted", 0)
                .orderByDesc("liked_count", "create_time"); // 按点赞数正序，创建时间倒序排列
        // 查询父评论
        Page<FreshNewsFatherComment> selectPage = freshNewsFatherCommentMapper.selectPage(pageParam, queryWrapper);
        List<FreshNewsFatherComment> freshNewsFatherCommentList = selectPage.getRecords();

        //用户是否点赞列表
        List<Boolean> isLikedList = new ArrayList<>(freshNewsFatherCommentList.size());

        List<FreshNewsFatherCommentResp> freshNewsFatherCommentRespList = freshNewsFatherCommentList
                .stream()
                .map(father -> {
                    FreshNewsFatherCommentResp fatherResp = new FreshNewsFatherCommentResp();
                    BeanUtils.copyProperties(father, fatherResp);

                    //合并redis中的点赞数
                    String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + fatherResp.getId() + ":1";
                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
                    Integer dbLike = fatherResp.getLikedCount();
                    fatherResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);

                    // 查询用户信息
                    User user = userMapper.selectById(father.getUserId());
                    fatherResp.setUserName(user==null|| user.getUsername()==null? father.getUserName() : user.getUsername());
                    fatherResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? father.getUserAvatar() : user.getAvatarUrl());

                    return fatherResp;
                })
                .sorted(Comparator.comparing(FreshNewsFatherCommentResp::getLikedCount).reversed())// 按点赞数降序排序
                .peek(fatherResp -> {
                    //判断用户是否点赞
                    String redisKey = RedisKeyConstant.LIKE_COMMENT + fatherResp.getId() + ":1";
                    Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
                    isLikedList.add(isLiked);
                })
                .toList();

        // 使用 ZINCRBY 命令增加有序集合中该 freshNewsId 的分数（浏览量）
        redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_ZSET_KEY, freshNewsId, 1);
        Boolean hasExpire = redisTemplate.getExpire(VIEW_COUNT_ZSET_KEY, TimeUnit.SECONDS) > 0;
        if (Boolean.FALSE.equals(hasExpire)) {
            redisTemplate.expire(VIEW_COUNT_ZSET_KEY, EXPIRATION, TimeUnit.SECONDS);
        }

        // 返回分页查询结果
        FreshNewsCommentResp response = new FreshNewsCommentResp(
                freshNewsId,                            // 新鲜事ID
                freshNewsFatherCommentRespList.size(),  // 一级评论数量
                1,                                      // 是否显示评论区(默认显示)
                freshNewsFatherCommentRespList,         // 一级评论列表
                isLikedList);                           // 一级评论是否点赞列表
        return ResultVo.success(response);

//        // 创建分页对象
//        Page<FreshNewsComment> pageParam = new Page<>(page, pageSize);
//
//        // 查询一级评论（parent_id = 0L）并按创建时间倒序排列
//        Page<FreshNewsComment> pageResult = freshNewsCommentMapper.selectPage(
//                pageParam,
//                new QueryWrapper<FreshNewsComment>()
//                        .eq("news_id", freshNewsId)  // 根据 freshNewsId 过滤
//                        .eq("parent_id", 0)                // 过滤一级评论
//                        .eq("is_deleted", 0)               // 确保评论没有被删除
//                        .orderByDesc("liked", "create_time")        // 按点赞数正序，创建时间倒序排列
//        );
//        // 获取查询结果
//        List<FreshNewsComment> records = pageResult.getRecords();
//
//        // 将评论数据转换成响应对象
//        List<FreshNewsCommentResp> freshNewsCommentRespList = records.stream()
//                .map(freshNewsComment -> {
//                    FreshNewsCommentResp resp = new FreshNewsCommentResp();
//                    BeanUtils.copyProperties(freshNewsComment, resp);
//                    return resp;
//                })
//                .collect(Collectors.toList());
//        // 使用 ZINCRBY 命令增加有序集合中该 freshNewsId 的分数（浏览量）
//        redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_ZSET_KEY,freshNewsId,1);
//        Boolean hasExpire = redisTemplate.getExpire(VIEW_COUNT_ZSET_KEY, TimeUnit.SECONDS) > 0;
//        if (Boolean.FALSE.equals(hasExpire)) {
//            redisTemplate.expire(VIEW_COUNT_ZSET_KEY, EXPIRATION, TimeUnit.SECONDS);
//        }
//        // 返回分页查询结果
//        return ResultVo.success(freshNewsCommentRespList);
    }


    /**
     * 删除评论
     *
     * @param freshNewsId 新鲜事ID
     * @param commentId   评论ID
     * @param isParent    是否是父评论 (0:子评论, 1:父评论)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo<Void> deleteComment(Long freshNewsId, Long commentId, Integer isParent) {
        if (isParent == 0) {
            // 删除子评论
            UpdateWrapper<FreshNewsChildComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("fresh_news_id", freshNewsId)
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsChildCommentMapper.update(updateWrapper);

            if (result <= 0) {
                return ResultVo.fail(CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }

            // 子评论删除成功,更新父评论子评论数量
            FreshNewsChildComment childComment = freshNewsChildCommentMapper.selectById(commentId);
            FreshNewsFatherComment fatherComment = freshNewsFatherCommentMapper.selectById(childComment.getFatherCommentId());
            // 检查父评论是否存在
            if (fatherComment == null) {
                return ResultVo.fail(CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 更新父评论子评论数量
            int updated = freshNewsFatherCommentMapper.updateById(fatherComment.setChildCount(fatherComment.getChildCount() - 1));
            if (updated <= 0) {
                // 更新父评论子评论数量失败,回滚子评论删除
                throw new RuntimeException(CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 子评论删除成功，返回成功结果
            return ResultVo.success();
        } else {
            // 删除父评论
            UpdateWrapper<FreshNewsFatherComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("fresh_news_id", freshNewsId)
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsFatherCommentMapper.update(updateWrapper);

            if (result <= 0) {
                // 父评论删除失败
                return ResultVo.fail(COMMENT_DELETE_FAILED_MESSAGE);
            }

            try {
                //获取子评论个数
                Integer childCount = freshNewsFatherCommentMapper.selectById(commentId).getChildCount();

                // 删除关联子评论
                UpdateWrapper<FreshNewsChildComment> childUpdateWrapper = new UpdateWrapper<>();
                childUpdateWrapper.eq("fresh_news_id", freshNewsId)
                        .eq("father_comment_id", commentId)
                        .eq("is_deleted", 0)
                        .set("is_deleted", 1);
                int childUpdate = freshNewsChildCommentMapper.update(childUpdateWrapper);

                if (childUpdate == childCount) {
                    // 关联子评论删除成功
                    return ResultVo.success();
                } else {
                    // 关联子评论删除失败，回滚父评论删除
                    throw new RuntimeException(CHILD_COMMENT_DELETE_FAILED_MESSAGE);
                }
            } catch (Exception e) {
                // 关联子评论删除失败，回滚父评论删除
                throw new RuntimeException(e);
            }
        }
//        int result = freshNewsCommentMapper.deleteByIdAndUserId(commentId, userId);
//        if (result > 0) {
//            return ResultVo.success(null);
//        } else {
//            return ResultVo.fail(COMMENT_DELETE_FAILED_MESSAGE);
//        }
    }

    /**
     * 回复评论（添加子评论）
     */
    @Override
    public ResultVo<Long> addReply(FreshNewsCommentRequest freshNewsCommentRequest) {
        User user = userMapper.selectById(freshNewsCommentRequest.getUserId());
        if (user == null) {
            // 用户不存在
            return ResultVo.fail(BAD_REQUEST_CODE, USER_NOT_FOUND_MESSAGE);
        }

        if (freshNewsCommentRequest.getParentId() == 0L) {
            // 父评论不存在,不是子评论
            return ResultVo.fail(BAD_REQUEST_CODE, NOT_CHILD_COMMENT);
        }

        FreshNewsChildComment freshNewsChildComment = new FreshNewsChildComment()
                .setFreshNewsId(freshNewsCommentRequest.getNewsId())    // 关联的新鲜事ID
                .setFatherCommentId(freshNewsCommentRequest.getParentId())  // 关联的父评论ID
                .setLikedCount(0)                                       // 点赞数量
                .setContent(freshNewsCommentRequest.getContent())       // 评论内容
                .setUserId(freshNewsCommentRequest.getUserId())         // 用户ID
                .setUserName(user.getAccount() == null ? "长理学子" : user.getAccount())   // 用户名
                .setUserAvatar(user.getAvatarUrl())                     // 用户头像URL
                .setCommentIp(freshNewsCommentRequest.getCommentIp())   // 评论发布的地址
                .setCommentTime(LocalDateTime.now())                    // 评论发布的时间
                .setIsActive(0)                                         // 评论是否有效: 0-未有效, 1-已有效
                .setIsDeleted(0)                                        // 评论是否删除: 0-未删除, 1-已删除
                .setCreateTime(LocalDateTime.now())                     // 创建时间
                .setUpdateTime(LocalDateTime.now());                    // 更新时间
        int result = freshNewsChildCommentMapper.insert(freshNewsChildComment);
        if (result <= 0) {
            // 子评论添加失败
            return ResultVo.fail(COMMENT_ADD_FAILED_MESSAGE);
        }
        //父评论回复数量+1
        FreshNewsFatherComment fatherComment = freshNewsFatherCommentMapper.selectById(freshNewsCommentRequest.getParentId());
        if (fatherComment == null) {
            // 父评论不存在
            return ResultVo.fail(BAD_REQUEST_CODE, PARENT_COMMENT_NOT_FOUND_MESSAGE);
        }

        // 子评论回复数量+1
        fatherComment.setChildCount(fatherComment.getChildCount() + 1);
        freshNewsFatherCommentMapper.updateById(fatherComment);

        // 通过 RabbitMQ 发送评论消息到队列
        rabbitTemplate.convertAndSend("replyExchange", "reply", freshNewsChildComment);
        return ResultVo.success(freshNewsChildComment.getId());

//        // 创建一个新的评论对象
//        FreshNewsComment comment = new FreshNewsComment();
//        comment.setNewsId(freshNewsCommentRequest.getNewsId());
//        comment.setUserId(freshNewsCommentRequest.getUserId());
//        comment.setContent(freshNewsCommentRequest.getContent());
//        comment.setParentId(commentId);  // 设置为父评论ID
//
//        // 查询父评论的 root 值
//        FreshNewsComment parentComment = freshNewsCommentMapper.selectById(commentId);
//        if (parentComment == null) {
//            return ResultVo.fail(PARENT_COMMENT_NOT_FOUND_MESSAGE);
//        }
//
//        // 将子评论的 root 设置为父评论的 root
//        comment.setRoot(parentComment.getRoot() != null ? parentComment.getRoot() : commentId);
//
//        // 通过 RabbitMQ 发送评论消息到队列
//        rabbitTemplate.convertAndSend("replyExchange", "reply", comment);
//
//        int result = freshNewsCommentMapper.insert(comment);
//        if (result > 0) {
//            return ResultVo.success();
//        } else {
//            return ResultVo.fail(COMMENT_ADD_FAILED_MESSAGE);
//        }
    }


    /**
     * 获取某一级评论评论的所有子评论
     *
     * @param commentId 评论ID
     * @param page      页码
     * @param pageSize  每页大小
     * @return 子评论列表
     */
    @Override
    public ResultVo<FreshNewsCommentResp<FreshNewsChildCommentResp>> listReplies(Long freshNewsId, Long commentId, Integer userId, Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNewsChildComment> pageParam = new Page<>(page, pageSize);
        Page<FreshNewsChildComment> pageResult = freshNewsChildCommentMapper.selectPage(
                pageParam,
                new QueryWrapper<FreshNewsChildComment>()
                        .eq("fresh_news_id", freshNewsId)
                        .eq("father_comment_id", commentId)
                        .eq("is_deleted", 0)         // 确保评论没有被删除
                        .orderByDesc("liked_count", "create_time")
        );
        List<FreshNewsChildComment> freshNewsChildCommentList = pageResult.getRecords();

        // 点赞状态列表
        List<Boolean> isLikedList = new ArrayList<>(freshNewsChildCommentList.size());

        List<FreshNewsChildCommentResp> freshNewsChildCommentRespsList = freshNewsChildCommentList
                .stream()
                .map(child -> {
                    FreshNewsChildCommentResp childResp = new FreshNewsChildCommentResp();
                    BeanUtils.copyProperties(child, childResp);

                    //合并redis中的点赞数
                    String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + childResp.getId() + ":0";
                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
                    Integer dbLike = childResp.getLikedCount();
                    childResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);

                    // 查询用户信息
                    User user = userMapper.selectById(child.getUserId());
                    childResp.setUserName(user==null|| user.getUsername()==null? child.getUserName() : user.getUsername());
                    childResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? child.getUserAvatar() : user.getAvatarUrl());

                    return childResp;
                })
                .sorted(Comparator.comparingInt(FreshNewsChildCommentResp::getLikedCount).reversed()) // 按点赞数降序排序
                .peek(childResp -> {
                    //判断用户是否点赞
                    String redisKey = RedisKeyConstant.LIKE_COMMENT + childResp.getId() + ":0";
                    Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
                    isLikedList.add(isLiked);
                })
                .collect(Collectors.toList());

        // 返回分页查询结果
        FreshNewsCommentResp<FreshNewsChildCommentResp> response = new FreshNewsCommentResp<>(
                freshNewsId,                            // 新鲜事ID
                freshNewsChildCommentRespsList.size(),  // 子评论数量
                1,                                      // 是否显示评论区(默认显示)
                freshNewsChildCommentRespsList,         // 子评论列表
                isLikedList);                           // 子评论是否点赞列表
        return ResultVo.success(response);

//        // 创建分页对象
//        Page<FreshNewsComment> pageParam = new Page<>(page, pageSize);
//        Page<FreshNewsComment> pageResult = freshNewsCommentMapper.selectPage(
//                pageParam,
//                new QueryWrapper<FreshNewsComment>()
//                        .eq("root", commentId)
//                        .eq("is_deleted", 0)         // 确保评论没有被删除
//                        .orderByDesc("liked", "create_time")
//        );
//
//        // 获取查询结果
//        List<FreshNewsComment> records = pageResult.getRecords();
//
//        // 将评论数据转换成响应对象
//        List<FreshNewsCommentResp> freshNewsCommentRespList = records.stream()
//                .map(freshNewsComment -> {
//                    FreshNewsCommentResp resp = new FreshNewsCommentResp();
//                    BeanUtils.copyProperties(freshNewsComment, resp);
//                    return resp;
//                })
//                .collect(Collectors.toList());
//        return ResultVo.success(freshNewsCommentRespList);
    }
}
