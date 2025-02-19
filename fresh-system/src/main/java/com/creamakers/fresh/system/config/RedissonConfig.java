package com.creamakers.fresh.system.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.88.14:6379")
                .setPassword("3100433796")
                .setDatabase(1)
                .setTimeout(5000);
        // 返回 RedissonClient 实例
        return Redisson.create(config);
    }
}
