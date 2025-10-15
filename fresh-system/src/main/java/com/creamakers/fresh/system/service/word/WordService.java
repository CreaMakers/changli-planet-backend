package com.creamakers.fresh.system.service.word;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.creamakers.fresh.system.dao.WordAllowMapper;
import com.creamakers.fresh.system.dao.WordDenyMapper;
import com.creamakers.fresh.system.domain.dto.WordAllow;
import com.creamakers.fresh.system.domain.dto.WordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lhw
 * @description
 * @date 2025/9/19 18:16
 */
@Service
@Slf4j
public class WordService {

    @Resource
    private CachedWordDeny cachedWordDeny;
    @Resource
    private CachedWordAllow cachedWordAllow;
    @Resource
    private WordDenyMapper wordDenyMapper;
    @Resource
    private WordAllowMapper wordAllowMapper;
    @Resource
    private SensitiveWordBs sensitiveWordBs;

    public void refreshDeny(List<String> words) {
        List<String> filtered = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        for (String word : words) {
            if (!cachedWordDeny.deny.contains(word)) {
                //添加到拒绝列表
                filtered.add(word);
                cachedWordDeny.deny.add(word);
            }
            if(cachedWordAllow.allow.contains(word)){
                //从允许列表移除
                removed.add(word);
                cachedWordAllow.allow.remove(word);
            }
        }
        //添加到拒绝列表(新增敏感词)
        sensitiveWordBs.addWord(filtered);
        //从允许列表移除(删除白名单)
        sensitiveWordBs.removeWordAllow(removed);
        try {
            int inserted = wordDenyMapper.batchInsertWords(words);
            log.info("刷新敏感词汇成功，数量：{}，数据库插入：{}条", words.size(), inserted);
        } catch (Exception e) {
            log.error("数据库批量插入允许词汇失败", e);
        }
        if(!removed.isEmpty()){
            try {
                //即在允许列表中，又在拒绝列表中，删除允许列表里的词汇
                int deleted = wordAllowMapper.update(null, Wrappers.<WordAllow>lambdaUpdate().set(WordAllow::getIsDeleted, 1).in(WordAllow::getWord, removed));
                log.info("数据库删除允许词汇成功，数量：{}，数据库删除：{}条", removed.size(), deleted);
            }catch (Exception e){
                log.error("数据库批量删除允许词汇失败", e);
            }
        }
    }

    public void refreshAllow(List<String> words) {
        List<String> filtered = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        for (String word : words) {
            if (!cachedWordAllow.allow.contains(word)) {
                //添加到允许列表
                filtered.add(word);
                cachedWordAllow.allow.add(word);
            }
            if(cachedWordDeny.deny.contains(word)){
                //从拒绝列表移除
                removed.add(word);
                cachedWordDeny.deny.remove(word);
            }
        }
        //添加到允许列表(新增白名单)
        sensitiveWordBs.addWordAllow(filtered);
        //从拒绝列表移除(删除敏感词)
        sensitiveWordBs.removeWord(removed);
        try {
            int inserted = wordAllowMapper.batchInsertWords(words);
            log.info("刷新允许词汇成功，数量：{}，数据库插入：{}条", words.size(), inserted);
        } catch (Exception e) {
            log.error("数据库批量插入允许词汇失败", e);
        }
        if(!removed.isEmpty()){
            try {
                //即在允许列表中，又在拒绝列表中，删除拒绝列表里的词汇
                int deleted = wordDenyMapper.update(null, Wrappers.<WordDeny>lambdaUpdate().set(WordDeny::getIsDeleted, 1).in(WordDeny::getWord, removed));
                log.info("数据库删除拒绝词汇成功，数量：{}，数据库删除：{}条", removed.size(), deleted);
            }catch (Exception e){
                log.error("数据库批量删除拒绝词汇失败", e);
            }
        }
    }

    public boolean check(String text) {
        return sensitiveWordBs.contains(text);
    }

    public String replace(String text){
        return sensitiveWordBs.replace(text);
    }
}