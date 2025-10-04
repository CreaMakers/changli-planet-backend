package com.creamakers.fresh.system.service.word;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.fresh.system.dao.WordAllowMapper;
import com.creamakers.fresh.system.domain.dto.WordAllow;
import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhw
 * @description
 * @date 2025/9/19 16:46
 */
@Component
@Slf4j
public class CachedWordAllow implements IWordAllow, InitializingBean {

    @Resource
    private WordAllowMapper wordAllowMapper;

    public HashSet<String> allow = new HashSet<>();

    @Override
    public List<String> allow() {
        return this.allow.stream().toList();
    }

    //全量同步，初始化启用
    @Override
    public void afterPropertiesSet() {
        try {
            this.allow.addAll( wordAllowMapper.selectList(
                            Wrappers.<WordAllow>lambdaQuery().eq(WordAllow::getIsDeleted, 0)
                    ).stream()
                    .map(WordAllow::getWord)
                    .collect(Collectors.toList())
            );
            log.info("加载允许词汇成功，数量：{}", this.allow.size());
        } catch (Exception e) {
            log.error("加载允许词汇失败，使用空列表兜底", e);
            this.allow = new HashSet<>();
        }
    }
}