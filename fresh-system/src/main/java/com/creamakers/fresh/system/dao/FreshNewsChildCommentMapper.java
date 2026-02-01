package com.creamakers.fresh.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.creamakers.fresh.system.domain.dto.FreshNewsChildComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    /**
     * 根据新鲜事ID和父评论ID分页查询子评论ID列表
     * @param freshNewsId 新鲜事ID
     * @param commentId 父评论ID
     * @param offset 偏移量
     * @param pageSize 每页数量
     * */
    @Select("select id from fresh_news_child_comments " +
            "where fresh_news_id = #{freshNewsId} and father_comment_id = #{commentId} and is_deleted = 0 " +
            "order by create_time desc " +
            "limit #{offset},#{pageSize}")
    List<Long> listChildCommentById(@Param("freshNewsId") Long freshNewsId, @Param("commentId") Long commentId,
                                    @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
}
