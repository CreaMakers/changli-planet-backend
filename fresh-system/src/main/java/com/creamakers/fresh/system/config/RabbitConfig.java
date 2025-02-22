package com.creamakers.fresh.system.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct // 对象创建之后立即执行的方法——mq增强
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息成功发送到交换机。correlationData: {}", correlationData);
        } else {
            log.error("消息发送到交换机失败。原因: {}", cause);
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error("消息从交换机路由到队列失败。message: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                returnedMessage.getMessage(),
                returnedMessage.getReplyCode(),
                returnedMessage.getReplyText(),
                returnedMessage.getExchange(),
                returnedMessage.getRoutingKey());
    }

    // 声明交换机
    @Bean
    public Exchange collectNewsExchange() {
        return new DirectExchange("collectNewsExchange", true, false);
    }

    @Bean
    public Exchange commentExchange() {
        return new DirectExchange("commentExchange", true, false);
    }

    @Bean
    public Exchange replyExchange() {
        return new DirectExchange("replyExchange", true, false);
    }

    @Bean
    public Exchange likeNewsExchange() {
        return new DirectExchange("likeNewsExchange", true, false);
    }

    @Bean
    public Exchange likeCommentExchange() {
        return new DirectExchange("likeCommentExchange", true, false);
    }

    // 声明队列
    @Bean
    public Queue collectNewsQueue() {
        return new Queue("collectNewsQueue", true);
    }

    @Bean
    public Queue commentQueue() {
        return new Queue("commentQueue", true);
    }

    @Bean
    public Queue replyQueue() {
        return new Queue("replyQueue", true);
    }

    @Bean
    public Queue likeNewsQueue() {
        return new Queue("likeNewsQueue", true);
    }

    @Bean
    public Queue likeCommentQueue() {
        return new Queue("likeCommentQueue", true);
    }

    // 绑定队列到交换机
    @Bean
    public Binding bindingCollectNews() {
        return BindingBuilder.bind(collectNewsQueue())
                .to(collectNewsExchange())
                .with("collectNews") // 路由键
                .noargs();
    }

    @Bean
    public Binding bindingComment() {
        return BindingBuilder.bind(commentQueue())
                .to(commentExchange())
                .with("comment") // 路由键
                .noargs();
    }

    @Bean
    public Binding bindingReply() {
        return BindingBuilder.bind(replyQueue())
                .to(replyExchange())
                .with("reply") // 路由键
                .noargs();
    }

    @Bean
    public Binding bindingLikeNews() {
        return BindingBuilder.bind(likeNewsQueue())
                .to(likeNewsExchange())
                .with("likeNews") // 路由键
                .noargs();
    }

    @Bean
    public Binding bindingLikeComment() {
        return BindingBuilder.bind(likeCommentQueue())
                .to(likeCommentExchange())
                .with("likeComment") // 路由键
                .noargs();
    }

}
