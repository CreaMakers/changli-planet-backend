//package com.creamakers.fresh.system.config;
//
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**Configuration
// * RocketMQ 配置类
// */
//@Configuration
//public class RocketConfig {
//
//    // 从配置文件中读取 NameServer 地址
//    @Value("${rocketmq.name-server}")
//    private String nameServer;
//
//    // 从配置文件中读取生产者组名
//    @Value("${rocketmq.producer.group}")
//    private String producerGroup;
//
//    // 从配置文件中读取消费者组名
//    @Value("${rocketmq.consumer.group}")
//    private String consumerGroup;
//
//    // 从配置文件中读取消费的主题
//    @Value("${rocketmq.consumer.topic}")
//    private String consumerTopic;
//
//    /**
//     * 配置并初始化 RocketMQ 生产者
//     */
//    @Bean
//    public DefaultMQProducer defaultMQProducer() throws Exception {
//        // 创建生产者实例
//        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
//        // 设置 NameServer 地址
//        producer.setNamesrvAddr(nameServer);
//        // 启动生产者
//        producer.start();
//        System.out.println("RocketMQ Producer 已启动...");
//        return producer;
//    }
//
//    /**
//     * 配置并初始化 RocketMQ 消费者
//     */
//    @Bean
//    public DefaultMQPushConsumer defaultMQPushConsumer() throws Exception {
//        // 创建消费者实例
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
//        // 设置 NameServer 地址
//        consumer.setNamesrvAddr(nameServer);
//        // 订阅主题和标签（* 表示订阅所有标签）
//        consumer.subscribe(consumerTopic, "*");
//        // 设置消费模式（集群模式或广播模式）
//        consumer.setMessageModel(MessageModel.CLUSTERING);
//        // 启动消费者
//        consumer.start();
//        System.out.println("RocketMQ Consumer 已启动...");
//        return consumer;
//    }
//}
