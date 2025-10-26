package com.creamakers.fresh.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;

//    @PostConstruct // 对象创建之后立即执行的方法——mq增强
//    public void initRabbitTemplate(){
//        rabbitTemplate.setConfirmCallback(this);
//        rabbitTemplate.setReturnsCallback(this);
//    }
// 自定义 BeanPostProcessor，处理 RabbitTemplate 的增强逻辑
    @Bean
    public BeanPostProcessor rabbitTemplatePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RabbitTemplate) {
                    RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
                    // 这里执行原来 initRabbitTemplate 里的增强逻辑
                    rabbitTemplate.setConfirmCallback(RabbitConfig.this);
                    rabbitTemplate.setReturnsCallback(RabbitConfig.this);
                }
                return bean;
            }
        };
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

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        // 设置允许反序列化的类
        typeMapper.addTrustedPackages("com.creamakers.fresh.system.domain.dto"); // 包级别的信任（推荐）
        // 或精确到类（如果只需要信任单个类）：
        // typeMapper.addTrustedClass(com.creamakers.fresh.system.domain.dto.FreshNewsFatherComment.class);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    // 将转换器应用到监听容器工厂
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter()); // 使用自定义转换器
        return factory;
    }
}
