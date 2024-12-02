package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.ChatGroupMapper;
import com.creamakers.websystem.domain.dto.ChatGroup;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.ChatGroupResp;
import com.creamakers.websystem.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ChatGroupServiceImpl implements ChatGroupService{
    @Autowired
    ChatGroupMapper chatGroupMapper;
    @Override
    public ResultVo<List<ChatGroupResp>> getAllChatGroups(Integer page, Integer pageSize) {
        Page<ChatGroup> pageParam = new Page<>(page, pageSize);
        Page<ChatGroup> Page = chatGroupMapper.selectPage(pageParam, new QueryWrapper<ChatGroup>().eq("is_deleted", 0));
        List<ChatGroup> records = Page.getRecords();
        List<ChatGroupResp> chatGroupRespList = records.stream().map(this::convertToChatGroupResp).toList();
        return ResultVo.success(chatGroupRespList);
    }
    private ChatGroupResp convertToChatGroupResp(ChatGroup record) {
        ChatGroupResp chatGroupResp = new ChatGroupResp();
        BeanUtil.copyProperties(record, chatGroupResp);
        return chatGroupResp;
    }
}
