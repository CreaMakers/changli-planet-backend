package com.hayaizo.chatsystem.common.config;

import com.hayaizo.chatsystem.common.factorty.MyThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 线程池配置
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    /**
     * 项目共用线程池
     */
    public static final String CSUST_EXECUTOR = "csustExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return csustExecutor();
    }

    public Executor getSecureInvokeExecutor() {
        return csustExecutor();
    }

    @Primary
    @Bean(CSUST_EXECUTOR)
    public ThreadPoolTaskExecutor csustExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("mallchat-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//满了调用线程执行，认为重要任务
        executor.setThreadFactory(new MyThreadFactory(executor));
        try {
            executor.initialize();
            logger.info("csustExecutor 线程池初始化成功");
        } catch (Exception e) {
            logger.error("csustExecutor 线程池初始化失败", e);
        }
        return executor;
    }

    @Bean(WS_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);//支持同时推送1000人
        executor.setThreadNamePrefix("websocket-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());//满了直接丢弃，默认为不重要消息推送
        executor.setThreadFactory(new MyThreadFactory(executor));
        try {
            executor.initialize();
            logger.info("websocketExecutor 线程池初始化成功");
        } catch (Exception e) {
            logger.error("websocketExecutor 线程池初始化失败", e);
        }
        return executor;
    }
}