package com.creamakers.fresh.system.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.fresh.system.domain.dto.WordDeny;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordDenyMapper extends BaseMapper<WordDeny> {


    /**
     * 批量插入词汇
     * @param words 词汇列表
     * @return 插入记录数
     */
    @Insert({
            "<script>",
            "INSERT INTO word_deny (word, is_deleted, create_time, update_time)",
            "VALUES ",
            "<foreach collection='words' item='word' separator=','>",
            "(#{word}, 0, NOW(), NOW())",
            "</foreach>",
            "</script>"
    })
    int batchInsertWords(@Param("words") List<String> words);
}
