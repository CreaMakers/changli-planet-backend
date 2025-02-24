package com.creamakers.websystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.websystem.domain.dto.FreshNewsComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FreshNewsCommentMapper extends BaseMapper<FreshNewsComment> {

    /**
     * 根据 commentId 和 userId 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM fresh_news_comments WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("<script>" +
            "UPDATE fresh_news_comments " +
            "SET liked = CASE comment_id " +
            "<foreach collection='comments' item='comment' index='index' open='' separator=' ' close=''>" +
            "  WHEN #{comment.commentId} THEN #{comment.liked} " +
            "</foreach>" +
            "END " +
            "WHERE comment_id IN " +
            "<foreach collection='comments' item='comment' open='(' separator=',' close=')'>" +
            "#{comment.commentId}" +
            "</foreach>" +
            "</script>")
    int updateCommentLikeNum(@Param("comments") List<FreshNewsComment> comments);

}

