package com.creamakers.websystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creamakers.websystem.domain.dto.PostComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
}
