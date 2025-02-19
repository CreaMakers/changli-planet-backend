package com.creamakers.fresh.system.utils;


import com.creamakers.fresh.system.service.CommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ぼつち
 * Comment队列监听器
 */
@Service
@Slf4j
public class MqListener {

    @Resource
    private CommentService commentsService;

}
