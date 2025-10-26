package com.creamakers.fresh.system.utils;


import com.creamakers.fresh.system.dao.*;
import com.creamakers.fresh.system.domain.dto.*;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.NotificationResp;
import com.creamakers.fresh.system.service.CommentService;
import com.creamakers.fresh.system.service.FreshNewsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.creamakers.fresh.system.constants.CommonConst.DB_INSERT_NOTIFICATION_FAILED;

@Service
@Slf4j
public class MqListener {

    @Resource
    private CommentService commentsService;
//    @Resource
//    private FreshNewsCommentMapper freshNewsCommentMapper;
    @Resource
    private FreshNewsFatherCommentMapper freshNewsFatherCommentMapper;
    @Resource
    private FreshNewsChildCommentMapper freshNewsChildCommentMapper;
    @Resource
    private FreshNewsMapper freshNewsMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private WebSocketService webSocketService;
//通知类型，1为系统通知，2为收到的赞，3为@我，4为回复我的
    // 1. 处理新的评论
    @RabbitListener(queues = "commentQueue")
    public void handleNewComment(FreshNewsComment comments) {
        log.info("收到新的评论: {}", comments.getContent());

        // 查找新鲜事的创建者
        Long newsId = comments.getId();
        FreshNews freshNews = freshNewsMapper.selectById(newsId);
        Long userId = freshNews.getUserId();

        // 创建推送的通知消息
        //String notificationMessage = "有人评论了您的新鲜事: " + comments.getContent();
        Notification notification = new Notification();
        notification.setContent(comments.getContent())
                .setSenderId(1L)
                .setReceiverId(userId)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSendTime(LocalDateTime.now())
                .setNotificationType(4)
                .setDescription("新鲜事收到的评论");
        int insert = notificationMapper.insert(notification);
        if(insert<=0) ResultVo.fail(DB_INSERT_NOTIFICATION_FAILED);
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification,notificationResp);
        // 通过 WebSocket 向该用户发送通知
        webSocketService.sendMessageToUser(userId, notificationResp);
    }

    // 2. 处理新的回复
    @RabbitListener(queues = "replyQueue")
    public void handleNewReply(FreshNewsChildComment comments) {
        log.info("收到新的回复: {}", comments.getContent());
        Long f = comments.getFatherCommentId();
        FreshNewsFatherComment freshNewsFatherComment = freshNewsFatherCommentMapper.selectById(f);
        Long parentId = freshNewsFatherComment.getUserId();

        // 创建推送的通知消息
        Notification notification = new Notification();
        notification.setContent(comments.getContent())
                .setSenderId(1L) // 假设1L为系统或某个固定用户的ID
                .setReceiverId(parentId)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSendTime(LocalDateTime.now())
                .setNotificationType(4)
                .setDescription("评论收到回复");
        int insert = notificationMapper.insert(notification);
        if(insert<=0) ResultVo.fail(DB_INSERT_NOTIFICATION_FAILED);
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification, notificationResp);

        // 通过 WebSocket 向该用户发送通知
        webSocketService.sendMessageToUser(parentId, notificationResp);
    }


    // 3. 处理点赞新鲜事
    @RabbitListener(queues = "likeNewsQueue")
    public void handleLikeNews(Long newsId) {
        log.info("收到点赞新闻的消息: newsId={}", newsId);

        // 查找新鲜事的创建者
        FreshNews freshNews = freshNewsMapper.selectById(newsId);
        Long userId = freshNews.getUserId();

        // 创建推送的通知消息
        Notification notification = new Notification();
        notification.setContent("有人点赞了您的新鲜事")
                .setSenderId(1L) // 假设1L为系统或某个固定用户的ID
                .setReceiverId(userId)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSendTime(LocalDateTime.now())
                .setNotificationType(2)
                .setDescription("新鲜事收到点赞");
        int insert = notificationMapper.insert(notification);
        if(insert<=0) ResultVo.fail(DB_INSERT_NOTIFICATION_FAILED);
        // 转换为 NotificationResp
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification, notificationResp);

        // 通过 WebSocket 向该用户发送通知
        webSocketService.sendMessageToUser(userId, notificationResp);
    }


    // 4. 处理点赞评论
    @RabbitListener(queues = "likeCommentQueue")
    public void handleLikeComment(Long commentId,Long isParent) {
        log.info("收到点赞评论的消息: commentId={}", commentId);

        // 查找评论的创建者

        //FreshNewsComment comment = freshNewsCommentMapper.selectById(commentId);
        //Long userId = comment.getUserId();
        Long userId = null;
        if(isParent == 0){
            // 子评论
            FreshNewsChildComment freshNewsChildComment = freshNewsChildCommentMapper.selectById(commentId);
            userId = freshNewsChildComment.getUserId();
        }else {
            // 父评论
            FreshNewsFatherComment freshNewsFatherComment = freshNewsFatherCommentMapper.selectById(commentId);
            userId = freshNewsFatherComment.getUserId();
        }

        // 创建推送的通知消息
        Notification notification = new Notification();
        notification.setContent("有人点赞了您的评论")
                .setSenderId(1L) // 假设1L为系统或某个固定用户的ID
                .setReceiverId(userId)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSendTime(LocalDateTime.now())
                .setNotificationType(2)
                .setDescription("评论收到点赞");
        int insert = notificationMapper.insert(notification);
        if(insert<=0) ResultVo.fail(DB_INSERT_NOTIFICATION_FAILED);
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification, notificationResp);

        // 通过 WebSocket 向该用户发送通知
        webSocketService.sendMessageToUser(userId, notificationResp);
    }


    // 5. 处理收集新闻
    @RabbitListener(queues = "collectNewsQueue")
    public void handleCollectNews(Long newsId) {
        log.info("收到收集新闻的消息: newsId={}", newsId);

        // 查找新闻的创建者
        FreshNews freshNews = freshNewsMapper.selectById(newsId);
        Long userId = freshNews.getUserId();

        // 创建推送的通知消息
        Notification notification = new Notification();
        notification.setContent("有人收藏了您的新鲜事")
                .setSenderId(1L) // 假设1L为系统或某个固定用户的ID
                .setReceiverId(userId)
                .setIsRead(0)
                .setIsDeleted(0)
                .setSendTime(LocalDateTime.now())
                .setNotificationType(2)
                .setDescription("新鲜事被收藏");
        int insert = notificationMapper.insert(notification);
        if(insert<=0) ResultVo.fail(DB_INSERT_NOTIFICATION_FAILED);
        NotificationResp notificationResp = new NotificationResp();
        BeanUtils.copyProperties(notification, notificationResp);

        // 通过 WebSocket 向该用户发送通知
        webSocketService.sendMessageToUser(userId, notificationResp);
    }
}

