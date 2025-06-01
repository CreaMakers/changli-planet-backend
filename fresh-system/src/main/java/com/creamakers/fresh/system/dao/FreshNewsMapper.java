package com.creamakers.fresh.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FreshNewsMapper extends BaseMapper<FreshNews> {
    @Update("<script>" +
            "UPDATE fresh_news " +
            "SET liked = CASE fresh_news_id " +
            "<foreach collection='list' item='news' index='index' open='' separator=' ' close=''>" +
            "  WHEN #{news.freshNewsId} THEN #{news.liked} " +
            "</foreach>" +
            "END " +
            "WHERE fresh_news_id IN " +
            "<foreach collection='list' item='news' open='(' separator=',' close=')'>" +
            "#{news.freshNewsId}" +
            "</foreach>" +
            "</script>")
    int updateFreshNewsLiked(@Param("list") List<FreshNews> freshNews);

}

