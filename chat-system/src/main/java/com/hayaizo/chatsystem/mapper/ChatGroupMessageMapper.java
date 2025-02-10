package com.hayaizo.chatsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hayaizo.chatsystem.dto.request.ChatMessagePageReq;
import com.hayaizo.chatsystem.po.ChatGroupMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatGroupMessageMapper extends BaseMapper<ChatGroupMessage> {
}
