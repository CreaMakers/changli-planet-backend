package com.creamakers.websystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.websystem.domain.dto.FreshNewsChildComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 新鲜事子评论Mapper接口
 */
public interface FreshNewsChildCommentMapper extends BaseMapper<FreshNewsChildComment> {

    @Update("<script>" +
            "UPDATE fresh_news_child_comments " +
            "SET liked_count = CASE id " +
            "<foreach collection='comments' item='comment' index='index' open='' separator=' ' close=''>" +
            "  WHEN #{comment.id} THEN #{comment.likedCount} " +
            "</foreach>" +
            "END " +
            "WHERE id IN " +
            "<foreach collection='comments' item='comment' open='(' separator=',' close=')'>" +
            "#{comment.id}" +
            "</foreach>" +
            "</script>")
    int updateCommentLikeNum(@Param("comments") List<FreshNewsChildComment> comments);
}
