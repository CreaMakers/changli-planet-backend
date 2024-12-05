package com.creamakers.websystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.websystem.domain.dto.Post;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface PostMapper extends BaseMapper<Post> {
    @Delete("DELETE FROM post_comment WHERE post_id = #{postId} AND comment_id = #{commentId}")
    int deleteCommentByPostIdAndCommentId(@Param("postId") Long postId, @Param("commentId") Long commentId);
}
