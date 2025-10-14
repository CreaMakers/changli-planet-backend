package com.creamakers.fresh.system.config;

import com.creamakers.fresh.system.service.word.CachedWordAllow;
import com.creamakers.fresh.system.service.word.CachedWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordConfig {

    @Autowired
    private CachedWordDeny CachedWordDeny;
    @Autowired
    private CachedWordAllow CachedWordAllow;

    /**
     * 注册敏感词过滤器Bean（全局单例）
     * 可根据业务需求自定义配置
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .ignoreCase(true)                  // 忽略大小写（如"色情"和"SE情"视为相同）
                .ignoreWidth(true)                 // 忽略全角半角（如"１"和"1"视为相同）
                .ignoreNumStyle(true)             // 忽略数字形式（如"123"和"一二三"视为相同）
                .wordDeny(CachedWordDeny)
                .wordAllow(CachedWordAllow)
                .init();
    }
}
