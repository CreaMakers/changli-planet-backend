package com.hayaizo.chatsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hayaizo.chatsystem.po.ChatGroupUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatGroupUserMapper extends BaseMapper<ChatGroupUser> {
}
