package com.creamakers.fresh.system.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.creamakers.fresh.system.constants.CommonConst;
import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.context.UserContext;
import com.creamakers.fresh.system.dao.*;
import com.creamakers.fresh.system.domain.dto.*;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsCommentRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsChildCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsCommentResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsFatherCommentResp;
import com.creamakers.fresh.system.service.CommentService;
import com.creamakers.fresh.system.service.word.WordService;
import com.creamakers.fresh.system.utils.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class CommentServiceImpl implements CommentService {

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

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WordService wordService;

    @Autowired
    private UserAdminMapper userAdminMapper;

    // redis记录浏览量
    private static final String VIEW_COUNT_ZSET_KEY = "freshNews:viewCounts:zset";  // 定义 ZSET 键名
    private static final long EXPIRATION = 7L * 24 * 60 * 60; // 7天，单位为秒

    @Value("${planet.freshNews.needAdmin}")
    private boolean needAdmin;

    /**
     * 添加评论(非回复评论)
     *
     * @param freshNewsCommentRequest 评论添加请求
     * @return 结果
     */
    @Override
    public ResultVo<Long> addComment(FreshNewsCommentRequest freshNewsCommentRequest) {
        UserAdmin userAdmin = userAdminMapper.selectById(freshNewsCommentRequest.getUserId());
        if (needAdmin && (userAdmin == null || userAdmin.getIsAdmin() == 0)) {
            // 非管理员
            return ResultVo.fail(CommonConst.UNAUTHORIZED,CommonConst.COMMENT_NOT_ADMIN);
        }

        User user = userMapper.selectById(freshNewsCommentRequest.getUserId());
        if (user == null) {
            // 用户不存在
            return ResultVo.fail(CommonConst.USER_NOT_FOUND_MESSAGE);
        }

        if (freshNewsCommentRequest.getParentId() != 0L) {
            // 不是父评论,返回错误
            return ResultVo.fail(CommonConst.NOT_FATHER_COMMENT);
        }

        //敏感词过滤
        if(wordService.check(freshNewsCommentRequest.getContent())){
            // 包含敏感词，替换敏感词
            freshNewsCommentRequest.setContent(wordService.replace(freshNewsCommentRequest.getContent()));
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
            return ResultVo.fail(CommonConst.COMMENT_ADD_FAILED_MESSAGE);
        }
    }

    private FreshNewsFatherComment getFatherCommentById(Long fatherId){
        return freshNewsFatherCommentMapper.selectById(fatherId);
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
        //一级评论ID列表
        List<Long> freshNewsFatherCommentIdList = freshNewsFatherCommentMapper.listFatherCommentIds(freshNewsId,(page-1)*pageSize,pageSize);
        //用户是否点赞列表
        List<Boolean> isLikedList = new ArrayList<>(freshNewsFatherCommentIdList.size());
        //一级评论列表
        List<FreshNewsFatherCommentResp> freshNewsFatherCommentRespList = new ArrayList<>(freshNewsFatherCommentIdList.size());

        for(Long fatherId : freshNewsFatherCommentIdList){
            FreshNewsFatherComment fatherComment = redisUtil.queryWithPassThrough(
                    RedisKeyConstant.NEWS_FATHER_COMMENT_CACHE,fatherId,FreshNewsFatherComment.class,
                    1L,TimeUnit.DAYS,this::getFatherCommentById);

            if(fatherComment == null){
                continue;
            }

            //转换为响应对象
            FreshNewsFatherCommentResp fatherResp = new FreshNewsFatherCommentResp();
            BeanUtils.copyProperties(fatherComment, fatherResp);

            //合并redis中的点赞数
            String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + fatherResp.getId() + ":1";
            Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
            Integer dbLike = fatherResp.getLikedCount();
            fatherResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);

            // 查询作者信息
            // TODO 缓存查询用户信息，但是牵扯到用户信息的更新问题。还需考虑数据一致性问题，还修改用户更新信息时的逻辑。
            User user = userMapper.selectById(fatherComment.getUserId());
            fatherResp.setUserName(user==null|| user.getAccount()==null? fatherComment.getUserName() : user.getAccount());
            fatherResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? fatherComment.getUserAvatar() : user.getAvatarUrl());

            freshNewsFatherCommentRespList.add(fatherResp);
        }
        // 按点赞数降序排序
        freshNewsFatherCommentRespList.sort(Comparator.comparing(FreshNewsFatherCommentResp::getLikedCount).reversed());

        for(FreshNewsFatherCommentResp fatherCommentResp : freshNewsFatherCommentRespList){
            //判断当前用户是否点赞
            String redisKey = RedisKeyConstant.LIKE_COMMENT + fatherCommentResp.getId() + ":1";
            Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
            isLikedList.add(isLiked);
        }

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

//        // 构造父评论查询参数
//        Page<FreshNewsFatherComment> pageParam = new Page<>(page, pageSize);
//        QueryWrapper<FreshNewsFatherComment> queryWrapper = new QueryWrapper<>();
//        queryWrapper
//                .eq("fresh_news_id", freshNewsId)
//                .eq("is_deleted", 0)
//                .orderByDesc("liked_count", "create_time"); // 按点赞数正序，创建时间倒序排列
//        // 查询父评论
//        Page<FreshNewsFatherComment> selectPage = freshNewsFatherCommentMapper.selectPage(pageParam, queryWrapper);
//        List<FreshNewsFatherComment> freshNewsFatherCommentList = selectPage.getRecords();
//
//        //用户是否点赞列表
//        List<Boolean> isLikedList = new ArrayList<>(freshNewsFatherCommentList.size());
//
//        List<FreshNewsFatherCommentResp> freshNewsFatherCommentRespList = freshNewsFatherCommentList
//                .stream()
//                .map(father -> {
//                    FreshNewsFatherCommentResp fatherResp = new FreshNewsFatherCommentResp();
//                    BeanUtils.copyProperties(father, fatherResp);
//
//                    //合并redis中的点赞数
//                    String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + fatherResp.getId() + ":1";
//                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
//                    Integer dbLike = fatherResp.getLikedCount();
//                    fatherResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);
//
//                    // 查询用户信息
//                    User user = userMapper.selectById(father.getUserId());
//                    fatherResp.setUserName(user==null|| user.getAccount()==null? father.getUserName() : user.getAccount());
//                    fatherResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? father.getUserAvatar() : user.getAvatarUrl());
//
//                    return fatherResp;
//                })
//                .sorted(Comparator.comparing(FreshNewsFatherCommentResp::getLikedCount).reversed())// 按点赞数降序排序
//                .peek(fatherResp -> {
//                    //判断用户是否点赞
//                    String redisKey = RedisKeyConstant.LIKE_COMMENT + fatherResp.getId() + ":1";
//                    Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
//                    isLikedList.add(isLiked);
//                })
//                .toList();
//
//        // 使用 ZINCRBY 命令增加有序集合中该 freshNewsId 的分数（浏览量）
//        redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_ZSET_KEY, freshNewsId, 1);
//        Boolean hasExpire = redisTemplate.getExpire(VIEW_COUNT_ZSET_KEY, TimeUnit.SECONDS) > 0;
//        if (Boolean.FALSE.equals(hasExpire)) {
//            redisTemplate.expire(VIEW_COUNT_ZSET_KEY, EXPIRATION, TimeUnit.SECONDS);
//        }
//
//        // 返回分页查询结果
//        FreshNewsCommentResp response = new FreshNewsCommentResp(
//                freshNewsId,                            // 新鲜事ID
//                freshNewsFatherCommentRespList.size(),  // 一级评论数量
//                1,                                      // 是否显示评论区(默认显示)
//                freshNewsFatherCommentRespList,         // 一级评论列表
//                isLikedList);                           // 一级评论是否点赞列表
//        return ResultVo.success(response);
    }


    /**
     * 删除评论
     * @param commentId   评论ID
     * @param isParent    是否是父评论 (0:子评论, 1:父评论)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo<Void> deleteComment(Long commentId, Integer isParent) {
        //判断用户是否是评论的作者
        Long userId = UserContext.getUserId();
        FreshNewsComment comment;
        if(isParent == 0){
            comment = freshNewsChildCommentMapper.selectById(commentId);
        }else{
            comment = freshNewsFatherCommentMapper.selectById(commentId);
        }
        if(!comment.getUserId().equals(userId)){
            // 非评论作者,返回失败结果
            return ResultVo.fail(CommonConst.NOT_FRESH_NEWS_COMMENT_AUTHOR);
        }

        if (isParent == 0) {
            // 删除子评论
            UpdateWrapper<FreshNewsChildComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsChildCommentMapper.update(updateWrapper);

            if (result <= 0) {
                return ResultVo.fail(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }

            // 子评论删除成功,更新父评论子评论数量
            FreshNewsChildComment childComment = freshNewsChildCommentMapper.selectById(commentId);
            FreshNewsFatherComment fatherComment = freshNewsFatherCommentMapper.selectById(childComment.getFatherCommentId());
            // 检查父评论是否存在
            if (fatherComment == null) {
                return ResultVo.fail(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 更新父评论子评论数量
            int updated = freshNewsFatherCommentMapper.updateById(fatherComment.setChildCount(fatherComment.getChildCount() - 1));
            if (updated <= 0) {
                // 更新父评论子评论数量失败,回滚子评论删除
                throw new RuntimeException(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 删除子评论缓存
            redisUtil.deleteValue(RedisKeyConstant.NEWS_CHILD_COMMENT_CACHE + childComment.getId());
            // 删除父评论缓存
            redisUtil.deleteValue(RedisKeyConstant.NEWS_FATHER_COMMENT_CACHE + fatherComment.getId());
            return ResultVo.success();
        } else {
            // 删除父评论
            UpdateWrapper<FreshNewsFatherComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsFatherCommentMapper.update(updateWrapper);

            if (result <= 0) {
                // 父评论删除失败
                return ResultVo.fail(CommonConst.COMMENT_DELETE_FAILED_MESSAGE);
            }

            try {
                //获取子评论个数
                Integer childCount = freshNewsFatherCommentMapper.selectById(commentId).getChildCount();

                // 删除关联子评论
                UpdateWrapper<FreshNewsChildComment> childUpdateWrapper = new UpdateWrapper<>();
                childUpdateWrapper
                        .eq("father_comment_id", commentId)
                        .eq("is_deleted", 0)
                        .set("is_deleted", 1);
                int childUpdate = freshNewsChildCommentMapper.update(childUpdateWrapper);

                if (childUpdate == childCount) {
                    // 关联子评论删除成功
                    redisUtil.deleteValue(RedisKeyConstant.NEWS_FATHER_COMMENT_CACHE + commentId);//删除父评论缓存
                    return ResultVo.success();
                } else {
                    // 关联子评论删除失败，回滚父评论删除
                    throw new RuntimeException(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
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
        UserAdmin userAdmin = userAdminMapper.selectById(freshNewsCommentRequest.getUserId());
        if (needAdmin && (userAdmin == null || userAdmin.getIsAdmin() == 0)) {
            // 非管理员
            return ResultVo.fail(CommonConst.UNAUTHORIZED,CommonConst.COMMENT_NOT_ADMIN);
        }

        User user = userMapper.selectById(freshNewsCommentRequest.getUserId());
        if (user == null) {
            // 用户不存在
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.USER_NOT_FOUND_MESSAGE);
        }

        if (freshNewsCommentRequest.getParentId() == 0L) {
            // 父评论不存在,不是子评论
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.NOT_CHILD_COMMENT);
        }

        //敏感词过滤
        if(wordService.check(freshNewsCommentRequest.getContent())){
            // 包含敏感词，替换敏感词
            freshNewsCommentRequest.setContent(wordService.replace(freshNewsCommentRequest.getContent()));
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
            return ResultVo.fail(CommonConst.COMMENT_ADD_FAILED_MESSAGE);
        }
        //父评论回复数量+1
        FreshNewsFatherComment fatherComment = freshNewsFatherCommentMapper.selectById(freshNewsCommentRequest.getParentId());
        if (fatherComment == null) {
            // 父评论不存在
            return ResultVo.fail(CommonConst.BAD_REQUEST_CODE, CommonConst.PARENT_COMMENT_NOT_FOUND_MESSAGE);
        }

        // 子评论回复数量+1
        fatherComment.setChildCount(fatherComment.getChildCount() + 1);
        freshNewsFatherCommentMapper.updateById(fatherComment);
        //删除父评论缓存
        redisUtil.deleteValue(RedisKeyConstant.NEWS_FATHER_COMMENT_CACHE + fatherComment.getId());

        // 通过 RabbitMQ 发送评论消息到队列
        rabbitTemplate.convertAndSend("replyExchange", "reply", freshNewsChildComment);
        return ResultVo.success(freshNewsChildComment.getId());
    }

    private FreshNewsChildComment getChildCommentById(Long childId){
        return freshNewsChildCommentMapper.selectById(childId);
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
        List<Long> childCommentIds = freshNewsChildCommentMapper.listChildCommentById(freshNewsId, commentId, (page - 1) * pageSize, pageSize);

        List<Boolean> isLikedList = new ArrayList<>(childCommentIds.size());
        List<FreshNewsChildCommentResp> freshNewsChildCommentRespsList = new ArrayList<>(childCommentIds.size());

        for(Long childId : childCommentIds){
            FreshNewsChildComment childComment = redisUtil.queryWithPassThrough(
                    RedisKeyConstant.NEWS_CHILD_COMMENT_CACHE, childId, FreshNewsChildComment.class,
                    1L, TimeUnit.DAYS, this::getChildCommentById);

            FreshNewsChildCommentResp childResp = new FreshNewsChildCommentResp();
            BeanUtils.copyProperties(childComment, childResp);

            //合并redis中的点赞数
            String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + childResp.getId() + ":0";
            Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
            Integer dbLike = childResp.getLikedCount();
            childResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);

            // 查询作者信息
            User user = userMapper.selectById(childComment.getUserId());
            childResp.setUserName(user==null|| user.getAccount()==null? childComment.getUserName() : user.getAccount());
            childResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? childComment.getUserAvatar() : user.getAvatarUrl());

            freshNewsChildCommentRespsList.add(childResp);
        }
        freshNewsChildCommentRespsList.sort(Comparator.comparingInt(FreshNewsChildCommentResp::getLikedCount).reversed());
        for (FreshNewsChildCommentResp childResp : freshNewsChildCommentRespsList){
            //判断当前用户是否点赞
            String redisKey = RedisKeyConstant.LIKE_COMMENT + childResp.getId() + ":0";
            Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
            isLikedList.add(isLiked);
        }

        // 返回分页查询结果
        FreshNewsCommentResp<FreshNewsChildCommentResp> response = new FreshNewsCommentResp<>(
                freshNewsId,                            // 新鲜事ID
                freshNewsChildCommentRespsList.size(),  // 子评论数量
                1,                                      // 是否显示评论区(默认显示)
                freshNewsChildCommentRespsList,         // 子评论列表
                isLikedList);                           // 子评论是否点赞列表
        return ResultVo.success(response);

//        // 创建分页对象
//        Page<FreshNewsChildComment> pageParam = new Page<>(page, pageSize);
//        Page<FreshNewsChildComment> pageResult = freshNewsChildCommentMapper.selectPage(
//                pageParam,
//                new QueryWrapper<FreshNewsChildComment>()
//                        .eq("fresh_news_id", freshNewsId)
//                        .eq("father_comment_id", commentId)
//                        .eq("is_deleted", 0)         // 确保评论没有被删除
//                        .orderByDesc("liked_count", "create_time")
//        );
//        List<FreshNewsChildComment> freshNewsChildCommentList = pageResult.getRecords();
//
//        // 点赞状态列表
//        List<Boolean> isLikedList = new ArrayList<>(freshNewsChildCommentList.size());
//
//        List<FreshNewsChildCommentResp> freshNewsChildCommentRespsList = freshNewsChildCommentList
//                .stream()
//                .map(child -> {
//                    FreshNewsChildCommentResp childResp = new FreshNewsChildCommentResp();
//                    BeanUtils.copyProperties(child, childResp);
//
//                    //合并redis中的点赞数
//                    String redisKey = RedisKeyConstant.LIKE_COMMENT_NUM + childResp.getId() + ":0";
//                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(redisKey);
//                    Integer dbLike = childResp.getLikedCount();
//                    childResp.setLikedCount(redisLiked == null ? dbLike : redisLiked + dbLike);
//
//                    // 查询用户信息
//                    User user = userMapper.selectById(child.getUserId());
//                    childResp.setUserName(user==null|| user.getAccount()==null? child.getUserName() : user.getAccount());
//                    childResp.setUserAvatar(user==null|| user.getAvatarUrl()==null? child.getUserAvatar() : user.getAvatarUrl());
//
//                    return childResp;
//                })
//                .sorted(Comparator.comparingInt(FreshNewsChildCommentResp::getLikedCount).reversed()) // 按点赞数降序排序
//                .peek(childResp -> {
//                    //判断用户是否点赞
//                    String redisKey = RedisKeyConstant.LIKE_COMMENT + childResp.getId() + ":0";
//                    Boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);
//                    isLikedList.add(isLiked);
//                })
//                .collect(Collectors.toList());
//
//        // 返回分页查询结果
//        FreshNewsCommentResp<FreshNewsChildCommentResp> response = new FreshNewsCommentResp<>(
//                freshNewsId,                            // 新鲜事ID
//                freshNewsChildCommentRespsList.size(),  // 子评论数量
//                1,                                      // 是否显示评论区(默认显示)
//                freshNewsChildCommentRespsList,         // 子评论列表
//                isLikedList);                           // 子评论是否点赞列表
//        return ResultVo.success(response);
    }
}
