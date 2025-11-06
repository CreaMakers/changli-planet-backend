package com.creamakers.fresh.system.service.word;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.fresh.system.dao.WordDenyMapper;
import com.creamakers.fresh.system.domain.dto.WordDeny;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
 * @date 2025/9/19 14:58
 */
@Component
@Slf4j
public class CachedWordDeny implements IWordDeny, InitializingBean {

    @Resource
    private WordDenyMapper wordDenyMapper;

    public HashSet<String> deny = new HashSet<>();

    @Override
    public List<String> deny() {
        return this.deny.stream().toList();
    }

    //全量同步 初始化启用
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.deny.addAll( wordDenyMapper.selectList(
                            Wrappers.<WordDeny>lambdaQuery().eq(WordDeny::getIsDeleted, 0)
                    ).stream()
                    .map(WordDeny::getWord)
                    .collect(Collectors.toList())
            );
            log.info("加载拒绝词汇成功，数量：{}", this.deny.size());
        } catch (Exception e) {
            log.error("加载拒绝词汇失败，使用空列表兜底", e);
            this.deny = new HashSet<>();
        }
    }
}